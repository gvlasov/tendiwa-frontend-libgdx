package org.tendiwa.client.ui.actors;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.model.CursorPosition;

@Singleton
public class CursorActor extends Actor {
private final Texture texture;
private final CursorPosition cursorPosition;

@Inject
CursorActor(CursorPosition cursorPosition) {
	this.cursorPosition = cursorPosition;
	texture = buildCursorTexture();
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
	batch.draw(
		getTexture(),
		cursorPosition.getWorldX() * GameScreen.TILE_SIZE,
		cursorPosition.getWorldY() * GameScreen.TILE_SIZE
	);
}
}
