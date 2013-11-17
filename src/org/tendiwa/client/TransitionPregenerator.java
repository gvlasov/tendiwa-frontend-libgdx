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
import tendiwa.core.CardinalDirection;
import tendiwa.core.Tendiwa;

/**
 * Creates and returns images of something gradually transitioning to something else. Once an instance of
 * TransitionPregenerator is created, all transition images are generated. After that you can fetch from here images
 * corresponding to a particular transition.
 */
public abstract class TransitionPregenerator {
final static int TILE_SIZE = GameScreen.TILE_SIZE;
public final ShaderProgram halfTransparencyShader;
final ShaderProgram shader;
final SpriteBatch batch;
private final TextureRegion[] textures;
final FrameBuffer fbo;
final SpriteBatch textureDrawingBatch = new SpriteBatch();
final Texture fboTexture;
private final int variationsPerTransition;
private final int totalTransitionTextures;
private final int regionsPerRow;

public TransitionPregenerator(int variationsPerTransition) {
	this.variationsPerTransition = variationsPerTransition;
	totalTransitionTextures = variationsPerTransition * 4;
	textures = new TextureRegion[totalTransitionTextures];
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
		(int) Math.ceil(Math.sqrt(totalTransitionTextures * TILE_SIZE * TILE_SIZE))
	);
	regionsPerRow = fboDimension / TILE_SIZE;
	fbo = new FrameBuffer(Pixmap.Format.RGBA8888, fboDimension, fboDimension, false);

	OrthographicCamera camera = new OrthographicCamera(GameScreen.TILE_SIZE * 16, GameScreen.TILE_SIZE);
	camera.setToOrtho(true);
	textureDrawingBatch.setProjectionMatrix(camera.combined);
	fboTexture = fbo.getColorBufferTexture();
	createTransitions();
	halfTransparencyShader = GameScreen.createShader(Gdx.files.internal("shaders/fovHalfTransparency.f.glsl"));
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
	int end = (dir.getCardinalIndex() + 1) * variationsPerTransition;
	for (int i = dir.getCardinalIndex() * variationsPerTransition; i < end; i++) {
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

/**
 * Creates a new transition Pixmap to a specified side.
 *
 * @param direction
 * 	To which side to transit.
 * @return A new Pixmap with an image of transition to that side.
 */
public abstract Pixmap createTransition(CardinalDirection direction);

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
	int hashShift = (screenY % variationsPerTransition + screenX) % variationsPerTransition;
	int index = cardinalIndex * variationsPerTransition + hashShift;
	return textures[index];
}

public TextureRegion getTransition(CardinalDirection dir, int screenX, int screenY) {
	int hashShift = (screenY % variationsPerTransition + screenX) % variationsPerTransition;
	int index = dir.getCardinalIndex() * variationsPerTransition + hashShift;
	return textures[index];
}
}