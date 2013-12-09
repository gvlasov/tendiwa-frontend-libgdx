package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TendiwaStage extends Stage {

private final GameScreen gameScreen;
private Map<Character, CharacterActor> characterActors = new HashMap<>();
private Actor playerCharacterActor;
private Map<Item, ItemActor> itemActors = new HashMap<>();

TendiwaStage(GameScreen gameScreen) {
	super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, gameScreen.batch);
	this.gameScreen = gameScreen;
	setCamera(gameScreen.camera);
	initializeActors();
}

private void initializeActors() {
	TimeStream timeStream = gameScreen.backendWorld.getPlayer().getTimeStream();
	for (Character character : timeStream.getCharacters()) {
		CharacterActor actor = createCharacterActor(character);
		characterActors.put(character, actor);
		if (character == Tendiwa.getPlayerCharacter()) {
			playerCharacterActor = actor;
		}
	}
	if (playerCharacterActor == null) {
		throw new RuntimeException("Player character actor has not been initialized");
	}
}

private CharacterActor createCharacterActor(Character character) {
	CharacterActor actor = new CharacterActor(character);
	addActor(actor);
	return actor;
}

public CharacterActor getCharacterActor(Character character) {
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
		assert getActors().contains(itemActors.get(item), true);
		return itemActors.get(item);
	} else {
		assert !getActors().contains(itemActors.get(item), true);
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

/**
 * Creates an {@link ItemActor} with flying action already added to it.
 *
 * @param item
 * 	Item to animate.
 * @param fromX
 * 	X coordinate of flight start cell in world coordinates.
 * @param fromY
 * 	Y coordinate of flight start cell in world coordinates.
 * @param toX
 * 	X coordinate of flight end cell in world coordinates.
 * @param toY
 * 	Y coordinate of flight end cell in world coordinates.
 * @return A new ItemActor with MoveToAction and call to {@link org.tendiwa.client.GameScreen#signalEventProcessingDone()}
 *         added to it.
 */
public ItemActor obtainFlyingItemActor(final Item item, int fromX, int fromY, int toX, int toY) {
	final ItemActor actor = obtainItemActor(fromX, fromY, item);
	MoveToAction moveToAction = new MoveToAction();
	moveToAction.setPosition(toX, toY);
	moveToAction.setDuration((float) (EnhancedPoint.distance(fromX, fromY, toX, toY) * 0.05));
	Action action = sequence(parallel(moveToAction, rotateBy(360, moveToAction.getDuration())), run(new Runnable() {
		@Override
		public void run() {
			TendiwaStage.this.removeItemActor(item);
			gameScreen.signalEventProcessingDone();
		}
	}));
	actor.addAction(action);
	return actor;
}
}
