package org.tendiwa.client.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.core.EnhancedPoint;

@Singleton
public class CursorActor extends Actor {
private final Texture texture;
private final GameScreenViewport viewport;
private int worldX;
private int worldY;

@Inject
CursorActor(GameScreenViewport viewport) {
	this.viewport = viewport;
	texture = buildCursorTexture();
}

void setWorldCoords(int worldX, int worldY) {
	this.worldX = worldX;
	this.worldY = worldY;
}
void updateCursorCoords() {
	EnhancedPoint point = viewport.screenPixelToWorldCell(Gdx.input.getX(), Gdx.input.getY());
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

@Override
public void draw(Batch batch, float parentAlpha) {
//	batch.begin();
	batch.draw(getTexture(), getWorldX() * GameScreen.TILE_SIZE, getWorldY() * GameScreen.TILE_SIZE);
//	batch.end();
}
}
