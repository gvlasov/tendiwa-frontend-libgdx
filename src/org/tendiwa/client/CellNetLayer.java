package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class CellNetLayer {
private final Batch batch;
private final GameScreenViewport viewport;
private final OrthographicCamera oneTileWiderCanera;
private FrameBuffer cellNetFramebuffer;

@Inject
CellNetLayer(@Named("game_screen_batch") Batch batch, GameScreenViewport viewport) {
	this.batch = batch;
	this.viewport = viewport;
	cellNetFramebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getWindowWidthPixels() + GameScreen.TILE_SIZE, viewport.getWindowHeightPixels() + GameScreen.TILE_SIZE, false);
	oneTileWiderCanera = new OrthographicCamera(Gdx.graphics.getWidth() + GameScreen.TILE_SIZE, Gdx.graphics.getHeight() + GameScreen.TILE_SIZE);
	oneTileWiderCanera.setToOrtho(true, viewport.getWindowWidthPixels() + GameScreen.TILE_SIZE, viewport.getWindowHeightPixels() + GameScreen.TILE_SIZE);
	buildNet();
}

/**
 * Draws the net to a dedicated framebuffer.
 */
void buildNet() {
	cellNetFramebuffer.begin();

	// Imitating the same camera as for batch, but with a greater viewport.
	OrthographicCamera adHocCamera = new OrthographicCamera(viewport.getWindowWidthPixels() + GameScreen.TILE_SIZE, viewport.getWindowHeightPixels() + GameScreen.TILE_SIZE);
	adHocCamera.setToOrtho(true, viewport.getWindowWidthPixels() + GameScreen.TILE_SIZE, viewport.getWindowHeightPixels() + GameScreen.TILE_SIZE);

	ShapeRenderer shapeRenderer = new ShapeRenderer();
	shapeRenderer.setProjectionMatrix(adHocCamera.combined);
	shapeRenderer.setColor(0, 0, 0, 0.1f);
	shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	for (int cellX = 0; cellX < viewport.getWindowWidthPixels() / GameScreen.TILE_SIZE + 2; cellX++) {
		shapeRenderer.line(cellX * GameScreen.TILE_SIZE, 0, cellX * GameScreen.TILE_SIZE, viewport.getWindowHeightPixels() + GameScreen.TILE_SIZE);
	}
	for (int cellY = 0; cellY < viewport.getWindowHeightCells() / GameScreen.TILE_SIZE + 2; cellY++) {
		shapeRenderer.line(0, cellY * GameScreen.TILE_SIZE, viewport.getWindowWidthPixels() + GameScreen.TILE_SIZE, cellY * GameScreen.TILE_SIZE);
	}
	shapeRenderer.end();
	cellNetFramebuffer.end();
}

/**
 * Copies contents of the dedicated framebuffer to the {@link GameScreen}'s batch.
 */
void draw() {
	batch.begin();
	batch.draw(cellNetFramebuffer.getColorBufferTexture(), viewport.getStartCellX() * GameScreen.TILE_SIZE, viewport.getStartCellY() * GameScreen.TILE_SIZE);
	batch.end();
}
}
