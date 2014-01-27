package org.tendiwa.client.markers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.client.CellSelection;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.TendiwaFonts;
import org.tendiwa.core.Border;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;

public class BorderMarker extends Actor {
private final static int markerWidth = 32;
private final CardinalDirection side;

public BorderMarker(Border border) {
	setX(border.x);
	setY(border.y);
	this.side = border.side;
}

@Override
public void draw(Batch batch, float parentAlpha) {
	float x = side != Directions.E ? getX() * GameScreen.TILE_SIZE : getX() * GameScreen.TILE_SIZE + GameScreen.TILE_SIZE - markerWidth;
	float y = side != Directions.S ? getY() * GameScreen.TILE_SIZE : getY() * GameScreen.TILE_SIZE + GameScreen.TILE_SIZE - markerWidth;
	float width = side.isVertical() ? GameScreen.TILE_SIZE : markerWidth;
	float height = side.isHorizontal() ? GameScreen.TILE_SIZE : markerWidth;
	batch.draw(
		CellSelection.getInstance().getTexture(),
		x,
		y,
		width,
		height
	);
	TendiwaFonts.default14Flipped.draw(batch, side.toString(), x, y);
}
}
