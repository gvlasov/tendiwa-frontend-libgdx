package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.tendiwa.core.EnhancedPoint;

public class Cursor {
private final Texture texture;
private final GameScreen gameScreen;
private int worldX;
private int worldY;

Cursor(GameScreen gameScreen) {
	this.gameScreen = gameScreen;

	texture = buildCursorTexture();
}

void updateCursorCoords() {
	EnhancedPoint point = gameScreen.screenPixelToWorldCell(Gdx.input.getX(), Gdx.input.getY());
	worldX = point.x;
	worldY = point.y;
}

public int getWorldY() {
	return worldY;
}

public int getWorldX() {
	return worldX;
}

Texture getTexture() {
	return texture;
}

private Texture buildCursorTexture() {
	Pixmap pixmap = new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(1, 1, 0, 0.3f);
	pixmap.fillRectangle(0, 0, 31, 31);
	return new Texture(pixmap);
}

public void draw() {
	gameScreen.batch.begin();
	gameScreen.batch.draw(getTexture(), getWorldX() * GameScreen.TILE_SIZE, getWorldY() * GameScreen.TILE_SIZE);
	gameScreen.batch.end();
}
}
