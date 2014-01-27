package org.tendiwa.client.markers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.client.TendiwaGame;

import java.util.Collection;
import java.util.LinkedList;

public class MarkersRegistry {
Collection<Actor> markers = new LinkedList<>();

public void clear() {
	for (Actor marker : markers) {
		TendiwaGame.getGameScreen().getStage().getRoot().removeActor(marker);
	}
	markers.clear();
}

public void add(Actor actor) {
	markers.add(actor);
	TendiwaGame.getGameScreen().getStage().addActor(actor);
}
}
