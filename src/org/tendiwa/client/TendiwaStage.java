package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;

public class TendiwaStage extends Stage {

private final GameScreen gameScreen;
private Map<Character, Actor> characterActors = new HashMap<>();
private Actor playerCharacterActor;
private Map<Item, ItemActor> itemActors = new HashMap<>();

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

/**
 * Returns an existing ItemActor for a {@link RememberedItem}, or creates a new ItemActor for a RememberedItem and
 * returns it.
 *
 * @param item
 * 	A remembered item.
 * @return An existing or a new ItemActor.
 */
public ItemActor obtainItemActor(int x, int y, Item item) {
	if (itemActors.containsKey(item)) {
		return itemActors.get(item);
	} else {
		ItemActor itemActor = new ItemActor(x, y, item);
		addActor(itemActor);
		itemActors.put(item, itemActor);
		return itemActor;
	}
}

public void removeItemActor(Item item) {
	getRoot().removeActor(itemActors.get(item));
	itemActors.remove(item);
}

public boolean hasActorForItem(Item item) {
	return itemActors.containsKey(item);
}
}
