package org.tendiwa.client.rendering.markers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.cellSelection.CellSelectionPlainActor;

public class CellMarker extends Actor {
CellMarker(int x, int y) {
	setX(x);
	setY(y);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	batch.draw(CellSelectionPlainActor.getTexture(), getX() * GameScreen.TILE_SIZE, getY() * GameScreen.TILE_SIZE);
}
}
