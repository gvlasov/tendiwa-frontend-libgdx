package org.tendiwa.client.ui.actors;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.GameScreen;
import org.tendiwa.core.Chunk;
import org.tendiwa.core.meta.CellPosition;
import org.tendiwa.core.meta.Coordinate;

@Singleton
public class CellSelectionPlainActor extends CellSelectionActor {
public static Texture texture;
private final CellPosition player;

@Inject
public CellSelectionPlainActor(@Named("player") CellPosition player) {
	this.player = player;
}

public static Texture getTexture() {
	if (texture == null) {
		Pixmap pixmap = new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888);
		pixmap.setColor(0, 1, 0, 0.3f);
		pixmap.fillRectangle(0, 0, GameScreen.TILE_SIZE - 1, GameScreen.TILE_SIZE - 1);
		texture = new Texture(pixmap);
	}
	return texture;
}

@Override
public void draw(Batch batch, float parentAlpha) {
	batch.begin();
	Coordinate[] vector = Chunk.vector(
		player.getX(),
		player.getY(),
		worldX,
		worldY
	);
	for (Coordinate coord : vector) {
		batch.draw(texture, coord.x * GameScreen.TILE_SIZE, coord.y * GameScreen.TILE_SIZE);
	}
	batch.end();
}
}