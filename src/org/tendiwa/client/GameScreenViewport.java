package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.google.inject.Singleton;
import org.tendiwa.core.EnhancedRectangle;
import org.tendiwa.core.meta.CellPosition;

@Singleton
public class GameScreenViewport {
private final int windowWidth;
private final int windowHeight;
private final int windowWidthCells;

public int getMaxStartX() {
	return maxStartX;
}

public int getMaxStartY() {
	return maxStartY;
}

private final int windowHeightCells;
private final int maxStartX;

public int getCameraMoveStep() {
	return cameraMoveStep;
}

private final int cameraMoveStep = 1;
private final int maxStartY;
private final int maxPixelX;
private final int maxPixelY;
private final OrthographicCamera camera;
private int startCellX;
private int startCellY;
private int centerPixelX;
private int centerPixelY;
private int startPixelX;
private int startPixelY;

GameScreenViewport(EnhancedRectangle world, CellPosition player) {
	windowWidth = Gdx.graphics.getWidth();
	windowHeight = Gdx.graphics.getHeight();
	windowWidthCells = (int) Math.ceil(((float) windowWidth) / GameScreen.TILE_SIZE);
	windowHeightCells = (int) Math.ceil(((float) windowHeight) / GameScreen.TILE_SIZE);
	maxStartX = world.getWidth() - windowWidthCells - cameraMoveStep;
	maxStartY = world.getHeight() - windowHeightCells - cameraMoveStep;
	maxPixelX = world.getWidth() * GameScreen.TILE_SIZE - windowWidth / 2;
	maxPixelY = world.getHeight() * GameScreen.TILE_SIZE - windowHeight / 2;
	camera = new OrthographicCamera(windowWidth, windowHeight);
	camera.setToOrtho(true, windowWidth, windowHeight);

	centerCamera(player.getX() * GameScreen.TILE_SIZE, player.getY() * GameScreen.TILE_SIZE);
	camera.update();
}

public int getWindowWidthPixels() {
	return windowWidth;
}

public int getWindowHeightPixels() {
	return windowHeight;
}

/**
 * Centers camera on a certain point of the world to render viewport around that point. Viewport will always be inside
 * world rectangle, so near world borders camera will be shifted back so viewport doesn't look at area outside the
 * world.
 *
 * @param x
 * 	X coordinate in pixels
 * @param y
 * 	Y coordinate in pixels
 */
void centerCamera(int x, int y) {
	if (x < windowWidth / 2) {
		x = windowWidth / 2;
	}
	if (y < windowHeight / 2) {
		y = windowHeight / 2;
	}
	if (x > maxPixelX) {
		x = maxPixelX;
	}
	if (y > maxPixelY) {
		y = maxPixelY;
	}
	startCellX = x / GameScreen.TILE_SIZE - windowWidthCells / 2 - windowWidthCells % 2;
	startCellY = y / GameScreen.TILE_SIZE - windowHeightCells / 2 - windowHeightCells % 2;
	centerPixelX = x;
	centerPixelY = y;
	startPixelX = centerPixelX - windowWidth / 2;
	startPixelY = centerPixelY - windowHeight / 2;
	camera.position.set(centerPixelX, centerPixelY, 0);
}

public int getWindowWidthCells() {
	return windowWidthCells;
}

public int getWindowHeightCells() {
	return windowHeightCells;
}

public int getMaxPixelX() {
	return maxPixelX;
}

public int getMaxPixelY() {
	return maxPixelY;
}

public OrthographicCamera getCamera() {
	return camera;
}

public int getCenterPixelX() {
	return centerPixelX;
}

public int getCenterPixelY() {
	return centerPixelY;
}

public int getStartCellX() {
	return startCellX;
}

public int getStartCellY() {
	return startCellY;
}

public int getStartPixelX() {
	return startPixelX;
}

public int getStartPixelY() {
	return startPixelY;
}

int getMaxRenderCellX() {
	return startCellX + windowWidthCells + (startPixelX % GameScreen.TILE_SIZE == 0 ? 0 : 1) + (windowWidthCells % 2 == 0 ? 0 : 1);
}

int getMaxRenderCellY() {
	return startCellY + windowHeightCells + (startPixelY % GameScreen.TILE_SIZE == 0 ? 0 : 1) + (windowHeightCells % 2 == 0 ? 0 : 1);
}
}

