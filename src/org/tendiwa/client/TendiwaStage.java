package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import tendiwa.core.Character;
import tendiwa.core.Tendiwa;
import tendiwa.core.TimeStream;

import java.util.HashMap;
import java.util.Map;

public class TendiwaStage extends Stage {

private final GameScreen gameScreen;
private Map<Character, Actor> characterActors = new HashMap<>();
private Actor playerCharacterActor;

TendiwaStage(GameScreen gameScreen) {
	super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, gameScreen.batch);
	this.gameScreen = gameScreen;
	setCamera(gameScreen.camera);
	initializeActors();
}

private void initializeActors() {
	TimeStream timeStream = gameScreen.backendWorld.getPlayerCharacter().getTimeStream();
	for (Character character : timeStream.getCharacters()) {
		Actor actor = createCharacterActor(character);
		characterActors.put(character, actor);
		if (character == Tendiwa.getPlayer()) {
			playerCharacterActor = actor;
		}
		addActor(actor);
	}
	if (playerCharacterActor == null) {
		throw new RuntimeException("Player character actor has not been initialized");
	}
}

private Actor createCharacterActor(Character character) {
	return new CharacterActor(character);
}

public Actor getCharacterActor(Character character) {
	return characterActors.get(character);
}
public Actor getPlayerCharacterActor() {
	return playerCharacterActor;
}
}
