package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import tendiwa.core.*;
import tendiwa.core.meta.Chance;

public class FovEdge {
final static int TILE_SIZE = GameScreen.TILE_SIZE;
private static final int VARIATIONS_PER_TRANSITION = 8;
private static final int TOTAL_TRANSITION_TEXTURES = VARIATIONS_PER_TRANSITION * 4;
final ShaderProgram shader;
final SpriteBatch batch;
final TextureRegion[] textures = new TextureRegion[TOTAL_TRANSITION_TEXTURES];
final FrameBuffer fbo;
final SpriteBatch textureDrawingBatch = new SpriteBatch();
final Texture fboTexture;
private final int regionsPerRow;
public final ShaderProgram halfTransparencyShader;

public FovEdge() {
	shader = new ShaderProgram(
		SpriteBatch.createDefaultShader().getVertexShaderSource(),
		Gdx.files.internal("shaders/fovTransition.f.glsl").readString()
	);
	ShaderProgram.pedantic = false;
	if (!shader.isCompiled()) {
		Tendiwa.getLogger().error(shader.getLog());
	}
	batch = new SpriteBatch();
	batch.setShader(shader);

	int fboDimension = MathUtils.nextPowerOfTwo(
		(int) Math.ceil(Math.sqrt(TOTAL_TRANSITION_TEXTURES * TILE_SIZE * TILE_SIZE))
	);
	regionsPerRow = fboDimension / TILE_SIZE;
	fbo = new FrameBuffer(Pixmap.Format.RGBA8888, fboDimension, fboDimension, false);

	OrthographicCamera camera = new OrthographicCamera(GameScreen.TILE_SIZE * 16, GameScreen.TILE_SIZE);
	camera.setToOrtho(true);
	textureDrawingBatch.setProjectionMatrix(camera.combined);
	fboTexture = fbo.getColorBufferTexture();
	createTransitions();
	halfTransparencyShader = new ShaderProgram(
		SpriteBatch.createDefaultShader().getVertexShaderSource(),
		Gdx.files.internal("shaders/fovHalfTransparency.f.glsl").readString()
	);
	if (!halfTransparencyShader.isCompiled()) {
		Tendiwa.getLogger().error(halfTransparencyShader.getLog());
		throw new RuntimeException();
	}
}

TextureRegion getFovTransition(boolean north, boolean east, boolean south, boolean west) {
	return textures[(north ? 1 : 0) + (east ? 2 : 0) + (south ? 4 : 0) + (west ? 8 : 0)];
}

private void createTransitions() {
	fbo.begin();
	textureDrawingBatch.begin();
	for (CardinalDirection dir : CardinalDirection.values()) {
		createTransitionVariants(dir);
	}
	textureDrawingBatch.end();
	fbo.end();
}

/**
 * Fills a TextureRegion with up to 4 transitions from cardinal sides.
 */
private void createTransitionVariants(CardinalDirection dir) {
	// Designate a new region of texture in framebuffer
	int end = (dir.getCardinalIndex() + 1) * VARIATIONS_PER_TRANSITION;
	for (int i = dir.getCardinalIndex() * VARIATIONS_PER_TRANSITION; i < end; i++) {
		int startX = (i % regionsPerRow) * TILE_SIZE;
		int startY = (i / regionsPerRow) * TILE_SIZE;
		textures[i] = new TextureRegion(
			fbo.getColorBufferTexture(),
			startX,
			startY,
			GameScreen.TILE_SIZE,
			GameScreen.TILE_SIZE
		);
		fboTexture.draw(createTransition(dir), startX, startY);
	}
}

private Pixmap createTransition(CardinalDirection dir) {
	int diffusionDepth = 13;
	if (dir.isVertical()) {
		dir = dir.opposite();
	}
	Pixmap.setBlending(Pixmap.Blending.None);
	Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(0, 0, 0, 1);
	pixmap.fill();
	CardinalDirection opposite = dir.opposite();
	EnhancedRectangle transitionRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(dir).createRectangle(diffusionDepth);
	EnhancedRectangle clearRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(opposite).createRectangle(TILE_SIZE - diffusionDepth);
	pixmap.setColor(0, 0, 0, 0);
	// Fill the most of the pixmap with transparent pixels.
	pixmap.fillRectangle(clearRec.x, clearRec.y, clearRec.width, clearRec.height);
	Segment sideSegment = transitionRec.getSideAsSegment(dir);
	EnhancedPoint point = new EnhancedPoint(sideSegment.x, sideSegment.y);
	pixmap.setColor(0, 0, 0, 0);
	CardinalDirection dynamicGrowingDir = dir.isVertical() ? Directions.E : Directions.S;
	int startI = sideSegment.getStaticCoord();
	int oppositeGrowing = opposite.getGrowing();
	int iterationsI = 0;
	for (
		int i = startI;
		i != startI + (diffusionDepth + 1) * opposite.getGrowing();
		i += oppositeGrowing
		) {
		for (
			int j = sideSegment.getStartCoord();
			j <= sideSegment.getEndCoord();
			j += 1
			) {
			if (Chance.roll((i - startI) / oppositeGrowing * 100 / diffusionDepth + 10)) {
				// Discard pixel (set it to be transparent)
				pixmap.drawPixel(point.x, point.y);
			}
			point.moveToSide(dynamicGrowingDir);
		}
		point.setLocation(sideSegment.x, sideSegment.y);
		point.moveToSide(opposite, iterationsI++);
	}
	return pixmap;
}

public void drawTransitions(SpriteBatch batch, int x, int y, boolean[] neighborSelected, int screenX, int screenY) {
	assert neighborSelected.length == 4;
	for (int i = 0; i < 4; i++) {
		if (neighborSelected[i]) {
			batch.draw(
				getTransition(i, screenX, screenY),
				x, y
			);
		}
	}
}

private TextureRegion getTransition(int cardinalIndex, int screenX, int screenY) {
	int hashShift = (screenY % VARIATIONS_PER_TRANSITION + screenX) % VARIATIONS_PER_TRANSITION;
	int index = cardinalIndex * VARIATIONS_PER_TRANSITION + hashShift;
	return textures[index];
}
}