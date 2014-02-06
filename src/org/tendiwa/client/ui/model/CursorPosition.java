package org.tendiwa.client.ui.model;

import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.core.EnhancedPoint;

@Singleton
public class CursorPosition {
private final GameScreenViewport viewport;
private int x = 0;
private int y = 0;

@Inject
public CursorPosition(GameScreenViewport viewport) {
	this.viewport = viewport;
}


void updateCursorCoords() {
	EnhancedPoint point = viewport.screenPixelToWorldCell(Gdx.input.getX(), Gdx.input.getY());
	x = point.x;
	y = point.y;
}

public void moveCursorBy(int dx, int dy) {
	x += dx;
	y += dy;
}

private void moveCursorTo(int x, int y) {
	this.x = x;
	this.y = y;
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
	x = point.x;
	y = point.y;
}
}
