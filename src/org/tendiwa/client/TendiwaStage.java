package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import tendiwa.core.*;
import tendiwa.core.Character;

import java.util.HashMap;
import java.util.Map;

public class TendiwaStage extends Stage {

private final GameScreen gameScreen;
private Map<Character, Actor> characterActors = new HashMap<>();
private Actor playerCharacterActor;
private Map<RememberedItem, ItemActor> itemActors = new HashMap<>();

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
	}
	if (playerCharacterActor == null) {
		throw new RuntimeException("Player character actor has not been initialized");
	}
}

private Actor createCharacterActor(Character character) {
	CharacterActor actor = new CharacterActor(character);
	addActor(actor);
	return actor;
}

public Actor getCharacterActor(Character character) {
	return characterActors.get(character);
}

public Actor getPlayerCharacterActor() {
	return playerCharacterActor;
}

public ItemActor getItemActor(Item item) {
	assert itemActors.containsKey(item);
	return itemActors.get(item);
}

public void createActorForItem(RememberedItem item) {
	if (itemActors.containsKey(item)) {
		throw new UnsupportedOperationException("Actor for item " + item + " has already been created");
	}
	ItemActor actor = new ItemActor(item);
	itemActors.put(item, actor);
	addActor(actor);
}

public void removeItemActor(Item item) {
	itemActors.remove(item);
}
}
