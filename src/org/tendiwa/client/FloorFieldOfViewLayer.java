package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import tendiwa.core.RenderCell;

public class FloorFieldOfViewLayer {
private final GameScreen gameScreen;
private final FovEdgeOpaque fovEdgeOpaque;
private ShapeRenderer shapeRen = new ShapeRenderer();

FloorFieldOfViewLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	fovEdgeOpaque = new FovEdgeOpaque();
}
public void draw() {

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
			RenderCell cell = gameScreen.getCell(x, y);
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
			RenderCell cell = gameScreen.getCell(x, y);
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
	shapeRen.rect(gameScreen.startCellX * GameScreen.TILE_SIZE, gameScreen.startCellY * GameScreen.TILE_SIZE, gameScreen.windowWidth + GameScreen.TILE_SIZE, gameScreen.windowHeight + GameScreen.TILE_SIZE);
	shapeRen.end();

	// Draw transitions to not yet seen cells
	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	fovEdgeOpaque.batch.setShader(GameScreen.defaultShader);
	fovEdgeOpaque.batch.begin();
	for (int x = gameScreen.startCellX; x < maxRenderCellX; x++) {
		for (int y = gameScreen.startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = gameScreen.getCell(x, y);
			if (cell != null) {
				int hashX, hashY;
				if (cell.isVisible()) {
					hashX = x + gameScreen.windowWidthCells - gameScreen.player.getX();
					hashY = y + gameScreen.windowHeightCells - gameScreen.player.getY();
				} else {
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
			}
		}
	}
	fovEdgeOpaque.batch.end();

	gameScreen.depthTestFrameBuffer.end();

	gameScreen.batch.begin();
	gameScreen.batch.draw(gameScreen.depthTestFrameBuffer.getColorBufferTexture(), gameScreen.startPixelX, gameScreen.startPixelY);
	gameScreen.batch.end();
}
private boolean[] getHasNotYetSeenNeighbors(int x, int y) {
	return new boolean[]{
		!gameScreen.hasCell(x, y - 1),
		!gameScreen.hasCell(x + 1, y),
		!gameScreen.hasCell(x, y + 1),
		!gameScreen.hasCell(x - 1, y)
	};
}

private boolean[] getHasUnseenNeighbors(int x, int y) {
	return new boolean[]{
		gameScreen.hasCell(x, y - 1) && !gameScreen.getCell(x, y - 1).isVisible(),
		gameScreen.hasCell(x + 1, y) && !gameScreen.getCell(x + 1, y).isVisible(),
		gameScreen.hasCell(x, y + 1) && !gameScreen.getCell(x, y + 1).isVisible(),
		gameScreen.hasCell(x - 1, y) && !gameScreen.getCell(x - 1, y).isVisible()
	};
}
}
