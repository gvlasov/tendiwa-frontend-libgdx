package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import org.tendiwa.events.EventProjectileFly;
import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TendiwaStage extends Stage {

private final GameScreen gameScreen;
private Map<Character, CharacterActor> characterActors = new HashMap<>();
private com.badlogic.gdx.scenes.scene2d.Actor playerCharacterActor;
private Map<Item, Actor> itemActors = new HashMap<>();

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

public com.badlogic.gdx.scenes.scene2d.Actor getPlayerCharacterActor() {
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
public Actor obtainItemActor(int x, int y, Item item) {
	ItemActor actor = new ItemActor(x, y, item);
	return actor;
}

public void removeItemActor(Item item) {
	getRoot().removeActor(itemActors.get(item));
}

public boolean hasActorForItem(Item item) {
	return itemActors.containsKey(item);
}

/**
 * Creates an {@link Actor} with flying action already added to it.
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
public Actor obtainFlyingProjectileActor(final Projectile item, int fromX, int fromY, int toX, int toY, EventProjectileFly.FlightStyle style) {
	final Actor actor;

	Action action;
	boolean rotating = false;
	if (style == EventProjectileFly.FlightStyle.CAST && item instanceof Item) {
		actor = obtainItemActor(fromX, fromY, (Item) item);
		rotating = true;
	} else if (item instanceof SpellProjectile) {
		actor = new SpellProjectileFireballActor(fromX, fromY);
	} else {
		actor = new ProjectileActor(item, fromX, fromY, toX, toY);
	}
	MoveToAction moveToAction = new MoveToAction();
	moveToAction.setPosition(toX, toY);
	moveToAction.setDuration((float) (EnhancedPoint.distance(fromX, fromY, toX, toY) * 0.05));
	ParallelAction movingAndRotating = parallel(moveToAction, rotateBy(360, moveToAction.getDuration()));
	RunnableAction runnable = run(new Runnable() {
		@Override
		public void run() {
			TendiwaStage.this.getRoot().removeActor(actor);
			gameScreen.signalEventProcessingDone();
		}
	});
	if (rotating) {
		action = sequence(movingAndRotating, runnable);
	} else {
		action = sequence(moveToAction, runnable);
	}

	actor.addAction(action);
	return actor;
}

public com.badlogic.gdx.scenes.scene2d.Actor obtainSoundActor(SoundType soundType, int x, int y) {
	final SoundActor actor = new SoundActor(soundType);
	actor.setPosition(
		x * GameScreen.TILE_SIZE - SoundActor.width / 2 + GameScreen.TILE_SIZE / 2,
		y * GameScreen.TILE_SIZE - SoundActor.width / 2 + GameScreen.TILE_SIZE / 2
	);
	actor.addAction(sequence(rotateBy(90, 0.3f), run(new Runnable() {
		@Override
		public void run() {
			TendiwaStage.this.getRoot().removeActor(actor);
			gameScreen.signalEventProcessingDone();
		}
	})));
	return actor;
}
}
