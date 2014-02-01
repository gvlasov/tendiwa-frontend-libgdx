package org.tendiwa.client.rendering.markers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.actors.CellSelectionPlainActor;
import org.tendiwa.core.Border;
import org.tendiwa.core.CardinalDirection;

public class BorderMarker extends Actor {
private final static int markerWidth = 4;
private final CardinalDirection side;

public BorderMarker(Border border) {
	setX(border.x);
	setY(border.y);
	this.side = border.side;
}

@Override
public void draw(Batch batch, float parentAlpha) {
	float x = getX() * GameScreen.TILE_SIZE - markerWidth / 2;
	float y = getY() * GameScreen.TILE_SIZE - markerWidth / 2;
	float width = side.isVertical() ? GameScreen.TILE_SIZE : markerWidth;
	float height = side.isHorizontal() ? GameScreen.TILE_SIZE : markerWidth;
	batch.draw(
		CellSelectionPlainActor.getTexture(),
		x,
		y,
		width,
		height
	);
}
}
