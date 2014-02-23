package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.core.Cell;
import org.tendiwa.core.World;
import org.tendiwa.core.meta.CellPosition;

@Singleton
public class GameScreenViewport {
private final int windowWidth;
private final int windowHeight;
private final int windowWidthCells;
private final int windowHeightCells;
private final int maxStartX;
private final int cameraMoveStep = 1;
private final int maxStartY;
private final int maxPixelX;
private final int maxPixelY;
private final OrthographicCamera camera;
private final CursorPosition cursorPosition;
private final Input gdxInput;
private int startCellX;
private int startCellY;
private int centerPixelX;
private int centerPixelY;
private int startPixelX;
private int startPixelY;

@Inject
GameScreenViewport(
	@Named("current_player_world") World world,
	@Named("player") CellPosition player,
    CursorPosition cursorPosition,
    Input gdxInput
) {
	this.cursorPosition = cursorPosition;
	this.gdxInput = gdxInput;
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

public int getMaxStartX() {
	return maxStartX;
}

public int getMaxStartY() {
	return maxStartY;
}

public int getCameraMoveStep() {
	return cameraMoveStep;
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
public void centerCamera(int x, int y) {
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
	cursorPosition.setPoint(screenPixelToWorldCell(gdxInput.getX(), gdxInput.getY()));
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

public int getMaxRenderCellX() {
	return startCellX + windowWidthCells + (startPixelX % GameScreen.TILE_SIZE == 0 ? 0 : 1) + (windowWidthCells % 2 == 0 ? 0 : 1);
}

public int getMaxRenderCellY() {
	return startCellY + windowHeightCells + (startPixelY % GameScreen.TILE_SIZE == 0 ? 0 : 1) + (windowHeightCells % 2 == 0 ? 0 : 1);
}

public boolean isInScreenRectangle(int x, int y, int startScreenCellX, int startScreenCellY, int widthInCells, int heightInCells) {
	return x >= startScreenCellX
		&& x < startScreenCellX + widthInCells
		&& y >= startScreenCellY
		&& y < startScreenCellY + heightInCells;
}

public Cell screenPixelToWorldCell(int screenX, int screenY) {
	return new Cell(
		(startPixelX + screenX) / GameScreen.TILE_SIZE,
		(startPixelY + screenY) / GameScreen.TILE_SIZE
	);
}
}

