package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CellNetLayer {
private final GameScreen gameScreen;
private final OrthographicCamera oneTileWiderCanera;
private FrameBuffer cellNetFramebuffer;

CellNetLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	cellNetFramebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, gameScreen.windowWidth + GameScreen.TILE_SIZE, gameScreen.windowHeight + GameScreen.TILE_SIZE, false);
	oneTileWiderCanera = new OrthographicCamera(Gdx.graphics.getWidth() + GameScreen.TILE_SIZE, Gdx.graphics.getHeight() + GameScreen.TILE_SIZE);
	oneTileWiderCanera.setToOrtho(true, gameScreen.windowWidth + GameScreen.TILE_SIZE, gameScreen.windowHeight + GameScreen.TILE_SIZE);
	buildNet();
}

/**
 * Draws the net to a dedicated framebuffer.
 */
void buildNet() {
	cellNetFramebuffer.begin();

	// Imitating the same camera as for batch, but with a greater viewport.
	OrthographicCamera adHocCamera = new OrthographicCamera(gameScreen.windowWidth + GameScreen.TILE_SIZE, gameScreen.windowHeight + GameScreen.TILE_SIZE);
	adHocCamera.setToOrtho(true, gameScreen.windowWidth + GameScreen.TILE_SIZE, gameScreen.windowHeight + GameScreen.TILE_SIZE);

	ShapeRenderer shapeRenderer = new ShapeRenderer();
	shapeRenderer.setProjectionMatrix(adHocCamera.combined);
	shapeRenderer.setColor(0, 0, 0, 0.1f);
	shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	for (int cellX = 0; cellX < gameScreen.windowWidth / GameScreen.TILE_SIZE + 2; cellX++) {
		shapeRenderer.line(cellX * GameScreen.TILE_SIZE, 0, cellX * GameScreen.TILE_SIZE, gameScreen.windowHeight + GameScreen.TILE_SIZE);
	}
	for (int cellY = 0; cellY < gameScreen.windowWidth / GameScreen.TILE_SIZE + 2; cellY++) {
		shapeRenderer.line(0, cellY * GameScreen.TILE_SIZE, gameScreen.windowWidth + GameScreen.TILE_SIZE, cellY * GameScreen.TILE_SIZE);
	}
	shapeRenderer.end();
	cellNetFramebuffer.end();
}

/**
 * Copies contents of the dedicated framebuffer to the {@link GameScreen}'s batch.
 */
void draw() {
	gameScreen.batch.begin();
	gameScreen.batch.draw(cellNetFramebuffer.getColorBufferTexture(), gameScreen.startCellX * GameScreen.TILE_SIZE, gameScreen.startCellY * GameScreen.TILE_SIZE);
	gameScreen.batch.end();
}
}
