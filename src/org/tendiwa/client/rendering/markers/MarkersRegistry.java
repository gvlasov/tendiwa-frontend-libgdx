package org.tendiwa.client.rendering.markers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.RenderBorder;
import org.tendiwa.core.events.EventFovChange;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

import java.util.Collection;
import java.util.LinkedList;

@Singleton
public class MarkersRegistry {
private final Stage stage;
Collection<Actor> markers = new LinkedList<>();

@Inject
public MarkersRegistry(Observable model, @Named("tendiwa_stage") Stage stage) {
	this.stage = stage;
	model.subscribe(new Observer<EventFovChange>() {

		@Override
		public void update(EventFovChange event, EventEmitter<EventFovChange> emitter) {
			clear();
			for (RenderBorder border : event.seenBorders) {
				add(new BorderMarker(border));
			}
			emitter.done(this);
		}
	}, EventFovChange.class);
}

public void clear() {
	for (Actor marker : markers) {
		stage.getRoot().removeActor(marker);
	}
	markers.clear();
}

public void add(Actor actor) {
	markers.add(actor);
	stage.addActor(actor);
}
}
