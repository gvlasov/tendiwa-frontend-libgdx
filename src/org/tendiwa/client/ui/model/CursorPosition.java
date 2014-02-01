package org.tendiwa.client.ui.model;

import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.GameScreen;
import org.tendiwa.core.EnhancedPoint;

public class CursorPosition {
private final GameScreen gameScreen;
private EntitySelectionListener<EnhancedPoint> entitySelectionListener;
private int x = 0;
private int y = 0;
private boolean isActive = false;

@Inject
public CursorPosition(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
}

/**
 * Passes a cell that is currently under cursor to the listener that was assigned at {@link
 * CursorPosition#start(EntitySelectionListener)}
 */
private void selectCurrentCell() {
	entitySelectionListener.execute(new EnhancedPoint(x, y));
	disable();
}

void updateCursorCoords() {
	EnhancedPoint point = gameScreen.screenPixelToWorldCell(Gdx.input.getX(), Gdx.input.getY());
	x = point.x;
	y = point.y;
}

/**
 * Disables cell selection without actually selecting any cell. Sets current InputProcessor back to default {@link
 * org.tendiwa.client.GameScreen#inputMultiplexer}.
 */
private void disable() {
	entitySelectionListener = null;
	isActive = false;
	Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
}

public void moveCursorBy(int dx, int dy) {
	x += dx;
	y += dy;
}

private void moveCursorTo(int x, int y) {
	this.x = x;
	this.y = y;
}

public boolean isActive() {
	return isActive;
}

public int getWorldX() {
	return x;
}

public int getWorldY() {
	return y;
}

public EnhancedPoint getPoint() {
	return new EnhancedPoint(x, y);
}

public void setPoint(EnhancedPoint point) {

}
}
