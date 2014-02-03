package org.tendiwa.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Observable;

@Singleton
public class CharacterActorFactory {
private final Observable model;

@Inject
CharacterActorFactory(Observable model) {
	this.model = model;
}

public CharacterActor create(Character character) {
	return new CharacterActor(model, character);
}
}
