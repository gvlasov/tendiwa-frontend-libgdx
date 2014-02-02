package org.tendiwa.client;

import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Observable;

public class CharacterActorFactory {
private final Observable model;

CharacterActorFactory(Observable model) {
	this.model = model;
}

public CharacterActor create(Character character) {
	return new CharacterActor(model, character);
}
}
