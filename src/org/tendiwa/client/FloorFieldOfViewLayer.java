package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import tendiwa.core.RenderCell;

public class FloorFieldOfViewLayer {
private final GameScreen gameScreen;
private final FovEdgeOpaque fovEdgeOpaque;
private final Mesh fullScreenQuad;
private final ShapeRenderer shapeRen = new ShapeRenderer();
private final ShaderProgram notYetSeenShader;
private final Texture blackOpaqueTexture;
private int uNotYetSeenCellsAnimationState;
private int uNotYetSeenCellsTime;
private boolean renderNotYetSeenCells = false;

FloorFieldOfViewLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	fovEdgeOpaque = new FovEdgeOpaque();
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
	gameScreen.postProcessor.captureEnd();
	gameScreen.depthTestFrameBuffer.begin();

	fovEdgeOpaque.batch.setProjectionMatrix(gameScreen.camera.combined);
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_LESS);
	Gdx.gl.glDepthMask(true);
	Gdx.gl.glColorMask(false, false, false, false);

	shapeRen.setProjectionMatrix(gameScreen.camera.combined);
	shapeRen.begin(ShapeRenderer.ShapeType.Filled);
	shapeRen.setColor(1, 0, 0, 0.5f);
	int maxRenderCellX = gameScreen.getMaxRenderCellX();
	int maxRenderCellY = gameScreen.getMaxRenderCellY();
	for (int x = gameScreen.startCellX; x < maxRenderCellX; x++) {
		for (int y = gameScreen.startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = gameScreen.renderWorld.getCell(x, y);
			if (cell != null) {
				if (!cell.isVisible()) {
					shapeRen.rect(x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
				}
			}
		}
	}
	shapeRen.end();
	Gdx.gl.glColorMask(true, true, true, true);

	// Draw transitions to unseen cells (half-transparent)
	fovEdgeOpaque.batch.setShader(fovEdgeOpaque.halfTransparencyShader);
	fovEdgeOpaque.batch.begin();
	for (int x = gameScreen.startCellX; x < maxRenderCellX; x++) {
		for (int y = gameScreen.startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = gameScreen.renderWorld.getCell(x, y);
			if (cell != null && cell.isVisible()) {
				boolean[] hasUnseenNeighbors = getHasUnseenNeighbors(x, y);
				if (hasUnseenNeighbors[0] || hasUnseenNeighbors[1] || hasUnseenNeighbors[2] || hasUnseenNeighbors[3]) {
					fovEdgeOpaque.drawTransitions(
						fovEdgeOpaque.batch,
						x * GameScreen.TILE_SIZE,
						y * GameScreen.TILE_SIZE,
						hasUnseenNeighbors,
						x + gameScreen.windowWidthCells - gameScreen.player.getX(),
						y - gameScreen.windowHeightCells - gameScreen.player.getY()
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
		gameScreen.startCellX * GameScreen.TILE_SIZE,
		gameScreen.startCellY * GameScreen.TILE_SIZE,
		(gameScreen.startCellX + gameScreen.getMaxRenderCellX()) * GameScreen.TILE_SIZE,
		(gameScreen.startCellY + gameScreen.getMaxRenderCellY()) * GameScreen.TILE_SIZE);
	shapeRen.end();

	// Draw transitions to not yet seen cells
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glDepthFunc(GL10.GL_LESS);
	fovEdgeOpaque.batch.setShader(WallActor.writeOpaqueToDepthShader);
	fovEdgeOpaque.batch.begin();
	Gdx.gl.glDepthMask(true);
	for (int x = gameScreen.startCellX; x < maxRenderCellX; x++) {
		for (int y = gameScreen.startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = gameScreen.renderWorld.getCell(x, y);
			if (cell != null) {
				int hashX, hashY;
				if (cell.isVisible()) {
					// For visible cells
					hashX = x + gameScreen.windowWidthCells - gameScreen.player.getX();
					hashY = y + gameScreen.windowHeightCells - gameScreen.player.getY();
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

	gameScreen.depthTestFrameBuffer.end();
	gameScreen.postProcessor.captureNoClear();

	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	gameScreen.batch.begin();
	gameScreen.batch.draw(gameScreen.depthTestFrameBuffer.getColorBufferTexture(), gameScreen.startPixelX, gameScreen.startPixelY);
	gameScreen.batch.end();
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
		!gameScreen.renderWorld.hasCell(x, y - 1),
		!gameScreen.renderWorld.hasCell(x + 1, y),
		!gameScreen.renderWorld.hasCell(x, y + 1),
		!gameScreen.renderWorld.hasCell(x - 1, y)
	};
}

private boolean[] getHasUnseenNeighbors(int x, int y) {
	return new boolean[]{
		gameScreen.renderWorld.hasCell(x, y - 1) && !gameScreen.renderWorld.getCell(x, y - 1).isVisible(),
		gameScreen.renderWorld.hasCell(x + 1, y) && !gameScreen.renderWorld.getCell(x + 1, y).isVisible(),
		gameScreen.renderWorld.hasCell(x, y + 1) && !gameScreen.renderWorld.getCell(x, y + 1).isVisible(),
		gameScreen.renderWorld.hasCell(x - 1, y) && !gameScreen.renderWorld.getCell(x - 1, y).isVisible()
	};
}
}
