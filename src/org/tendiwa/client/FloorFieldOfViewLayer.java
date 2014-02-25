package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bitfire.postprocessing.PostProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.clients.RenderCell;
import org.tendiwa.core.clients.RenderWorld;
import org.tendiwa.core.meta.CellPosition;

@Singleton
public class FloorFieldOfViewLayer {
private final FovEdgeOpaque fovEdgeOpaque;
private final Mesh fullScreenQuad;
private final ShapeRenderer shapeRen = new ShapeRenderer();
private final ShaderProgram notYetSeenShader;
private final Texture blackOpaqueTexture;
private final Batch batch;
private final CellPosition player;
private final RenderWorld renderWorld;
private final FrameBuffer depthTestFrameBuffer;
private final PostProcessor postProcessor;
private final ShaderProgram halfTransparencyShader;
private final ShaderProgram writeOpaqueToDepthShader;
private final GameScreenViewport viewport;
private int uNotYetSeenCellsAnimationState;
private int uNotYetSeenCellsTime;
private boolean renderNotYetSeenCells = false;

@Inject
FloorFieldOfViewLayer(
	@Named("game_screen_batch") Batch batch,
	@Named("player") CellPosition player,
	RenderWorld renderWorld,
	@Named("game_screen_depth_test_fb") FrameBuffer depthTestFrameBuffer,
	@Named("game_screen_default_post_processor") PostProcessor postProcessor,
	@Named("shader_half_transparency") ShaderProgram halfTransparencyShader,
	@Named("shader_write_opaque_to_depth") ShaderProgram writeOpaqueToDepthShader,
	GameScreenViewport viewport,
	FovEdgeOpaque fovEdgeOpaque
) {
	this.batch = batch;
	this.player = player;
	this.renderWorld = renderWorld;
	this.depthTestFrameBuffer = depthTestFrameBuffer;
	this.postProcessor = postProcessor;
	this.halfTransparencyShader = halfTransparencyShader;
	this.writeOpaqueToDepthShader = writeOpaqueToDepthShader;
	this.viewport = viewport;
	this.fovEdgeOpaque = fovEdgeOpaque;
	fullScreenQuad = createFullScreenQuad();
	notYetSeenShader = new ShaderProgram(
		Gdx.files.internal("shaders/noTransformation.v.glsl"),
		Gdx.files.internal("shaders/notYetSeen.f.glsl")
	);
	blackOpaqueTexture = createBlackTexture();
	uNotYetSeenCellsAnimationState = notYetSeenShader.getUniformLocation("u_state");
	uNotYetSeenCellsTime = notYetSeenShader.getUniformLocation("time");
}

private Texture createBlackTexture() {
	Pixmap pixmap = new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(0, 0, 0, 1.0f);
	pixmap.fill();
	return new Texture(pixmap);
}

public void draw() {
	postProcessor.captureEnd();
	depthTestFrameBuffer.begin();

	fovEdgeOpaque.batch.setProjectionMatrix(viewport.getCamera().combined);
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_LESS);
	Gdx.gl.glDepthMask(true);
	Gdx.gl.glColorMask(false, false, false, false);

	shapeRen.setProjectionMatrix(viewport.getCamera().combined);
	shapeRen.begin(ShapeRenderer.ShapeType.Filled);
	shapeRen.setColor(1, 0, 0, 0.5f);
	int maxRenderCellX = viewport.getMaxRenderCellX();
	int maxRenderCellY = viewport.getMaxRenderCellY();
	for (int x = viewport.getStartCellX(); x < maxRenderCellX; x++) {
		for (int y = viewport.getStartCellY(); y < maxRenderCellY; y++) {
			RenderCell cell = renderWorld.getCurrentPlane().getCell(x, y);
			if (cell != null) {
				if (!cell.isVisible()) {
					shapeRen.rect(
						x * GameScreen.TILE_SIZE,
						y * GameScreen.TILE_SIZE,
						GameScreen.TILE_SIZE,
						GameScreen.TILE_SIZE
					);
				}
			}
		}
	}
	shapeRen.end();
	Gdx.gl.glColorMask(true, true, true, true);

	// Draw transitions to unseen cells (half-transparent)
	fovEdgeOpaque.batch.setShader(halfTransparencyShader);
	fovEdgeOpaque.batch.begin();
	for (int x = viewport.getStartCellX(); x < maxRenderCellX; x++) {
		for (int y = viewport.getStartCellY(); y < maxRenderCellY; y++) {
			RenderCell cell = renderWorld.getCurrentPlane().getCell(x, y);
			if (cell != null && cell.isVisible()) {
				boolean[] hasUnseenNeighbors = getHasUnseenNeighbors(x, y);
				if (hasUnseenNeighbors[0] || hasUnseenNeighbors[1] || hasUnseenNeighbors[2] || hasUnseenNeighbors[3]) {
					fovEdgeOpaque.drawTransitions(
						fovEdgeOpaque.batch,
						x * GameScreen.TILE_SIZE,
						y * GameScreen.TILE_SIZE,
						hasUnseenNeighbors,
						x + viewport.getWindowWidthCells() - player.getX(),
						y - viewport.getWindowHeightCells() - player.getY()
					);
				}
			}
		}
	}
	fovEdgeOpaque.batch.end();

	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_EQUAL);

	shapeRen.begin(ShapeRenderer.ShapeType.Filled);
	shapeRen.setColor(0, 0, 0, 0.4f);
	// Draw black transparent color above mask
	shapeRen.rect(
		viewport.getStartCellX() * GameScreen.TILE_SIZE,
		viewport.getStartCellY() * GameScreen.TILE_SIZE,
		(viewport.getStartCellX() + viewport.getMaxRenderCellX()) * GameScreen.TILE_SIZE,
		(viewport.getStartCellY() + viewport.getMaxRenderCellY()) * GameScreen.TILE_SIZE);
	shapeRen.end();

	// Draw transitions to not yet seenCells cells
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glDepthFunc(GL10.GL_LESS);
	fovEdgeOpaque.batch.setShader(writeOpaqueToDepthShader);
	fovEdgeOpaque.batch.begin();
	Gdx.gl.glDepthMask(true);
	for (int x = viewport.getStartCellX(); x < maxRenderCellX; x++) {
		for (int y = viewport.getStartCellY(); y < maxRenderCellY; y++) {
			RenderCell cell = renderWorld.getCurrentPlane().getCell(x, y);
			if (cell != null) {
				int hashX, hashY;
				if (cell.isVisible()) {
					// For visible cells
					hashX = x + viewport.getWindowWidthCells() - player.getX();
					hashY = y + viewport.getWindowHeightCells() - player.getY();
				} else {
					// For unseen cells
					hashX = x;
					hashY = y;
				}
				fovEdgeOpaque.drawTransitions(
					fovEdgeOpaque.batch,
					x * GameScreen.TILE_SIZE,
					y * GameScreen.TILE_SIZE,
					getHasNotYetSeenNeighbors(x, y),
					hashX,
					hashY
				);
			} else {
				fovEdgeOpaque.batch.draw(
					blackOpaqueTexture,
					x * GameScreen.TILE_SIZE,
					y * GameScreen.TILE_SIZE,
					GameScreen.TILE_SIZE,
					GameScreen.TILE_SIZE
				);
			}
		}
	}
	fovEdgeOpaque.batch.end();

	// Render not yet seen cells
//	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	if (renderNotYetSeenCells) {
		Gdx.gl.glDepthFunc(GL10.GL_EQUAL);
		notYetSeenShader.begin();
		notYetSeenShader.setUniformf(uNotYetSeenCellsAnimationState, computeNotYetSeenCellsAnimationState(1));
		notYetSeenShader.setUniformf(uNotYetSeenCellsTime, (float) ((System.currentTimeMillis() % 10000000) / 10000f));
		fullScreenQuad.render(notYetSeenShader, GL10.GL_TRIANGLE_FAN);
		notYetSeenShader.end();
	}

	depthTestFrameBuffer.end();
	postProcessor.captureNoClear();

	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	batch.begin();
	batch.draw(depthTestFrameBuffer.getColorBufferTexture(), viewport.getStartPixelX(), viewport.getStartPixelY());
	batch.end();
}

private float computeNotYetSeenCellsAnimationState(int frequency) {
	return (float) ((Math.PI * 2 / 2000 * frequency) * System.currentTimeMillis() % (Math.PI * 2));
}

public Mesh createFullScreenQuad() {

	float[] verts = new float[20];
	int i = 0;

	verts[i++] = -1; // x1
	verts[i++] = -1; // y1
	verts[i++] = 0;
	verts[i++] = 0f; // u1
	verts[i++] = 0f; // v1

	verts[i++] = 1f; // x2
	verts[i++] = -1; // y2
	verts[i++] = 0;
	verts[i++] = 1f; // u2
	verts[i++] = 0f; // v2

	verts[i++] = 1f; // x3
	verts[i++] = 1f; // y2
	verts[i++] = 0;
	verts[i++] = 1f; // u3
	verts[i++] = 1f; // v3

	verts[i++] = -1; // x4
	verts[i++] = 1f; // y4
	verts[i++] = 0;
	verts[i++] = 0f; // u4
	verts[i++] = 1f; // v4

	Mesh mesh = new Mesh(true, 4, 0,  // static mesh with 4 vertices and no indices
		new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
		new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

	mesh.setVertices(verts);
	return mesh;
}

private boolean[] getHasNotYetSeenNeighbors(int x, int y) {
	return new boolean[]{
		!renderWorld.getCurrentPlane().hasCell(x, y - 1),
		!renderWorld.getCurrentPlane().hasCell(x + 1, y),
		!renderWorld.getCurrentPlane().hasCell(x, y + 1),
		!renderWorld.getCurrentPlane().hasCell(x - 1, y)
	};
}

private boolean[] getHasUnseenNeighbors(int x, int y) {
	return new boolean[]{
		renderWorld.getCurrentPlane().hasCell(x, y - 1) && !renderWorld.getCurrentPlane().getCell(x, y - 1).isVisible(),
		renderWorld.getCurrentPlane().hasCell(x + 1, y) && !renderWorld.getCurrentPlane().getCell(x + 1, y).isVisible(),
		renderWorld.getCurrentPlane().hasCell(x, y + 1) && !renderWorld.getCurrentPlane().getCell(x, y + 1).isVisible(),
		renderWorld.getCurrentPlane().hasCell(x - 1, y) && !renderWorld.getCurrentPlane().getCell(x - 1, y).isVisible()
	};
}
}
