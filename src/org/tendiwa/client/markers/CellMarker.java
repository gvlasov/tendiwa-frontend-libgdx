package org.tendiwa.client.markers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.client.CellSelection;
import org.tendiwa.client.GameScreen;

public class CellMarker extends Actor {
CellMarker(int x, int y) {
	setX(x);
	setY(y);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	batch.draw(CellSelection.getInstance().getTexture(), getX() * GameScreen.TILE_SIZE, getY() * GameScreen.TILE_SIZE);
}
}
