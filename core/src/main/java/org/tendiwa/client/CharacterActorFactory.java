package org.tendiwa.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.World;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.ThreadProxy;

@Singleton
public class CharacterActorFactory {
private final Observable model;
private final World world;

@Inject
CharacterActorFactory(
	@Named("current_player_world") World world,
	ThreadProxy model
) {
	this.world = world;
	this.model = model;
}

public CharacterActor create(Character character) {
	return new CharacterActor(world, model, character);
}
}
