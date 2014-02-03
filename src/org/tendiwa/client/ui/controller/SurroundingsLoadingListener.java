package org.tendiwa.client.ui.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.dependencies.WorldProvider;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

@Singleton
public class SurroundingsLoadingListener {
@Inject
public SurroundingsLoadingListener(Observable model, final PlayerCharacterProvider playerCharacterProvider, final WorldProvider worldProvider) {
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, EventEmitter<EventInitialTerrain> emitter) {
			playerCharacterProvider.setCharacter(event.player);
			worldProvider.setWorld(event.world);
		}
	}, EventInitialTerrain.class);
}
}
