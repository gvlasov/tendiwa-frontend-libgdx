package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.utils.SnapshotArray;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TendiwaStage extends Stage {

private static Comparator<Actor> ySorter = new Comparator<Actor>() {
	@Override
	public int compare(Actor o1, Actor o2) {
		return -Math.round(o1.getY()) + Math.round(o2.getY());
	}
};
private final GameScreen gameScreen;
private Map<Character, CharacterActor> characterActors = new HashMap<>();
private com.badlogic.gdx.scenes.scene2d.Actor playerCharacterActor;
private Map<Item, Actor> itemActors = new HashMap<>();
private HashMap<Integer, WallActor> wallActors = new HashMap<>();
private Multimap<Integer, Actor> plane2actors = HashMultimap.create();
private Table<Integer, CardinalDirection, BorderObjectActor> borderObjectActors = HashBasedTable.create();

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

/**
 * Sets zIndexes of actors. This method is called whenever an actor is moved to another z-level. This method exists
 * because of a too simple z-index model of libgdx, where z-index can only be a value [0..numberOfActors).
 */
void sortActorsByY() {
	SnapshotArray<Actor> children = getRoot().getChildren();
	children.sort(ySorter);
	int index = 0;
	for (Actor child : children) {
		child.setZIndex(index);
	}
}

private CharacterActor createCharacterActor(Character character) {
	CharacterActor actor = new CharacterActor(character);
	if (Tendiwa.getPlayerCharacter().getSeer().canSee(character.getX(), character.getY())) {
		actor.setVisible(true);
	} else {
		actor.setVisible(false);
	}
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
 * Returns an existing ItemActor for a {@link org.tendiwa.core.RememberedItem}, or creates a new ItemActor for a RememberedItem and
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
	moveToAction.setDuration((float) (EnhancedPoint.distanceDouble(fromX, fromY, toX, toY) * 0.05));
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

public Actor obtainSoundActor(SoundType soundType, int x, int y) {
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

public void updateCharactersVisibility() {
	Character player = Tendiwa.getPlayerCharacter();
	for (Character character : player.getTimeStream().getCharacters()) {
		CharacterActor actor = gameScreen.getStage().getCharacterActor(character);
		if (actor != null) {
			if (player.canSee(character.getX(), character.getY())
				&& !actor.isVisible()) {
				actor.setVisible(true);
			} else if (!player.canSee(character.getX(), character.getY())
				&& actor.isVisible()) {
				actor.setVisible(false);
			}

		}
	}
}

/**
 * Creates a new WallActor and puts it in this Stage.
 *
 * @param x
 * 	X coordinate of an actor in world coordinates.
 * @param y
 * 	Y coordinate of an actor in world coordinates.
 */
public void addWallActor(int x, int y) {
	GameObject gameObject = gameScreen.getCurrentBackendPlane().getGameObject(x, y);
	WallActor actor = new WallActor(gameScreen, x, y, (WallType) gameObject);
	wallActors.put(getWallActorKey(x, y), actor);
//	actor.setVisible(false);
	addActor(actor);
	actor.setZIndex(y * Tendiwa.getWorldWidth() + x);
}

public void removeWallActor(int x, int y) {
	WallActor actor = wallActors.get(getWallActorKey(x, y));
	assert actor != null;
	getRoot().removeActor(actor);
}

/**
 * Returns a key under which a WallActor in a particular cell will be stored in {@link TendiwaStage#wallActors}.
 *
 * @param x
 * 	X coordinate of a wall in world coordinates.
 * @param y
 * 	Y coordinate of a wall in world coordinates.
 * @return A key which is a hash of 2 coordinates.
 */
private int getWallActorKey(int x, int y) {
	return x * gameScreen.backendWorld.getHeight() + y;
}

/**
 * Checks if there is an actor for a wall in a particular cell.
 *
 * @param x
 * 	X coordinate of a wall in world coordinates.
 * @param y
 * 	Y coordinate of a wall in world coordinates.
 * @return true if there is one, false otherwise.
 */
public boolean hasWallActor(int x, int y) {
	return wallActors.containsKey(getWallActorKey(x, y));
}

public void addObjectActor(int x, int y) {
	ObjectActor actor = new ObjectActor(x, y, gameScreen.getCurrentBackendPlane().getGameObject(x, y));
	plane2actors.put(gameScreen.getCurrentBackendPlane().getLevel(), actor);
	addActor(actor);
}

public void removeActorsOfPlane(int zLevel) {
	for (Actor actor : plane2actors.get(zLevel)) {
		getRoot().removeActor(actor);
	}
	plane2actors.removeAll(zLevel);
}

public WallActor getWallActor(int worldX, int worldY) {
	return wallActors.get(getWallActorKey(worldX, worldY));
}

public BorderObjectActor addBorderObjectActor(RenderBorder border) {
	BorderObjectActor actor = new BorderObjectActor(
		border.getX(),
		border.getY(),
		border.getSide(),
		border.getObject()
	);
	borderObjectActors.put(Chunk.cellHash(border.getX(), border.getY(), Tendiwa.getWorldHeight()), border.getSide(), actor);
	addActor(actor);
	return actor;
}

public void removeBorderObjectActor(int worldX, int worldY, CardinalDirection side) {
	BorderObjectActor removedActor = borderObjectActors.remove(Chunk.cellHash(worldX, worldY, Tendiwa.getWorldHeight()), side);
	getRoot().removeActor(removedActor);
}
}
