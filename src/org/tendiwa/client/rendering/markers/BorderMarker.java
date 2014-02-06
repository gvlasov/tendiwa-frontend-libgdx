package org.tendiwa.client.rendering.markers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.actors.CellSelectionPlainActor;
import org.tendiwa.core.Border;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.events.EventFovChange;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

public class BorderMarker extends Actor {
private final static int markerWidth = 4;
private final CardinalDirection side;

@Inject
BorderMarker(@Named("tendiwa") Observable model, @Assisted Border border) {
	setX(border.x);
	setY(border.y);
	this.side = border.side;
	model.subscribe(new Observer<EventFovChange>() {
		@Override
		public void update(EventFovChange event, EventEmitter<EventFovChange> emitter) {
			BorderMarker.this.getParent().removeActor(BorderMarker.this);
		}
	}, EventFovChange.class);
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
