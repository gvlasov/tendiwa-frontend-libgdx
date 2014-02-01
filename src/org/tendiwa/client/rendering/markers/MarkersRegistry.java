package org.tendiwa.client.rendering.markers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Collection;
import java.util.LinkedList;

public class MarkersRegistry {
private final Stage stage;
Collection<Actor> markers = new LinkedList<>();

public MarkersRegistry(Stage stage) {
	this.stage = stage;
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
