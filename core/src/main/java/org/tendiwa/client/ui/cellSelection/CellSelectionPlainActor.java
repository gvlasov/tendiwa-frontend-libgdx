package org.tendiwa.client.ui.cellSelection;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.BasicCellSegment;

@Singleton
public class CellSelectionPlainActor extends CellSelectionActor {
public static Texture texture;
private final Cell player;

@Inject
public CellSelectionPlainActor(@Named("player") Cell player, CursorPosition cursorPosition) {
	super(cursorPosition);
	this.player = player;
}

public Texture getTexture() {
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
//	batch.begin();
	BasicCell[] vector = BasicCellSegment.cells(
		player.x(),
		player.y(),
		cursorPosition.getWorldX(),
		cursorPosition.getWorldY()
	);
	for (BasicCell coord : vector) {
		batch.draw(getTexture(), coord.x() * GameScreen.TILE_SIZE, coord.y() * GameScreen.TILE_SIZE);
	}
//	batch.end();
}
}