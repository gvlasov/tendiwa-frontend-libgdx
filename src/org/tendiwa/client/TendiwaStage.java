package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.utils.SnapshotArray;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import org.tendiwa.client.rendering.effects.Blood;
import org.tendiwa.client.rendering.markers.MarkersRegistry;
import org.tendiwa.client.ui.factories.SoundActorFactory;
import org.tendiwa.client.ui.model.MessageLog;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.events.*;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;
import org.tendiwa.core.vision.Seer;

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
private final RenderWorld renderWorld;
private final GameScreen gameScreen;
private final EventProcessor eventProcessor;
private final SoundActorFactory soundActorFactory;
private final Seer playerSeer;
private final CharacterActorFactory characterActorFactory;
private final MarkersRegistry markersRegistry;
private Map<Character, CharacterActor> characterActors = new HashMap<>();
private com.badlogic.gdx.scenes.scene2d.Actor playerCharacterActor;
private Map<Item, Actor> itemActors = new HashMap<>();
private HashMap<Integer, WallActor> wallActors = new HashMap<>();
private Multimap<Integer, Actor> plane2actors = HashMultimap.create();
private Table<Integer, CardinalDirection, BorderObjectActor> borderObjectActors = HashBasedTable.create();

@Inject
TendiwaStage(CharacterActorFactory characterActorFactory, final MessageLog messageLog, final Game game, MarkersRegistry markersRegistry, final Character player, Observable model, final RenderWorld renderWorld, GameScreenViewport viewport, final GameScreen gameScreen, final EventProcessor eventProcessor, SoundActorFactory soundActorFactory, Seer playerSeer) {
	super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, gameScreen.batch);
	this.characterActorFactory = characterActorFactory;
	this.markersRegistry = markersRegistry;
	this.renderWorld = renderWorld;
	this.gameScreen = gameScreen;
	this.eventProcessor = eventProcessor;
	this.soundActorFactory = soundActorFactory;
	this.playerSeer = playerSeer;
	setCamera(viewport.getCamera());
	initializeActors();
	model.subscribe(new Observer<EventFovChange>() {

		@Override
		public void update(EventFovChange event, EventEmitter<EventFovChange> emitter) {
			for (Integer coord : event.unseenCells) {
//				if (gameScreen.getCurrentBackendPlane().hasWall(cell.x, cell.y)) {
//					gameScreen.getStage().removeWallActor(cell.x, cell.y);
//				}
			}
			for (RenderCell cell : event.seenCells) {
				assert player.getPlane().containsCell(cell.x, cell.y) : cell;
				if (player.getPlane().hasWall(cell.x, cell.y)) {
					if (!hasWallActor(cell.x, cell.y)) {
						addWallActor(cell.x, cell.y);
					}
				} else if (player.getPlane().hasObject(cell.x, cell.y)) {
					addObjectActor(cell.x, cell.y);
				}
			}
			for (RenderBorder border : event.seenBorders) {
				if (!renderWorld.getCurrentPlane().hasUnseenBorderObject(border)) {
					if (border.getObject() != null) {
						addBorderObjectActor(border);
					}
				}
			}
			for (Border border : event.unseenBorders) {
			}
			emitter.done(this);
		}
	}, EventFovChange.class);
	model.subscribe(new Observer<EventMove>() {
		@Override
		public void update(EventMove event, EventEmitter<EventMove> emitter) {
			Actor characterActor = getCharacterActor(event.character);
			int index = event.character.getY() * Tendiwa.getWorldWidth() + event.character.getX();
			sortActorsByY();

			if (gameScreen.getConfig().animationsEnabled) {
				Action action;
				if (event.movingStyle == MovingStyle.STEP) {
					action = new MoveToAction();
					((MoveToAction) action).setPosition(event.character.getX(), event.character.getY());
					((MoveToAction) action).setDuration(0.1f);
				} else if (event.movingStyle == MovingStyle.LEAP) {
					MoveByAction moveTo = new MoveByAction();
					moveTo.setAmount(event.character.getX() - event.xPrev, event.character.getY() - event.yPrev);
					float lengthMovingDuration = 0.3f;
					moveTo.setDuration(lengthMovingDuration);
					MoveByAction moveUp = moveBy(0, -1, lengthMovingDuration / 2);
					moveUp.setInterpolation(Interpolation.exp5Out);
					MoveByAction moveDown = moveBy(0, 1, lengthMovingDuration / 2);
					moveDown.setInterpolation(Interpolation.exp5In);
					Action upAndDown = sequence(moveUp, moveDown);
					action = parallel(moveTo, upAndDown);
				} else {
					action = moveTo(event.character.getX(), event.character.getY(), 0.1f);
				}
				Action sequence = sequence(action, run(new Runnable() {
					@Override
					public void run() {
						updateCharactersVisibility();
						eventProcessor.processOneMoreEventInCurrentFrame();
						eventProcessor.signalEventProcessingDone();
					}
				}));
				characterActor.addAction(sequence);
			} else {
				characterActor.setX(event.character.getX());
				characterActor.setY(event.character.getY());
				updateCharactersVisibility();
				if (event.character.isPlayer()) {
					// If this is player moving, then the next event will be
					// EventFovChange, and to prevent flickering we make the current event
					// render in the same frame as the previous event.
					eventProcessor.processOneMoreEventInCurrentFrame();
				}
				eventProcessor.signalEventProcessingDone();
			}
		}
	}, EventMove.class);
	model.subscribe(new Observer<EventInitialTerrain>() {

		@Override
		public void update(EventInitialTerrain event, EventEmitter<EventInitialTerrain> emitter) {
			game.setScreen(gameScreen);
			for (RenderCell cell : event.seenCells) {
				renderWorld.getCurrentPlane().seeCell(cell);
				HorizontalPlane plane = gameScreen.getCurrentBackendPlane();
				if (plane.hasWall(cell.x, cell.y)) {
					addWallActor(cell.x, cell.y);
				} else if (plane.hasObject(cell.x, cell.y)) {
					addObjectActor(cell.x, cell.y);
				}

			}
			for (RenderBorder border : event.seenBorders) {
				if (border.getObject() != null) {
					addBorderObjectActor(border);
				}
			}
			eventProcessor.signalEventProcessingDone();
		}
	}, EventInitialTerrain.class);
	model.subscribe(new Observer<EventItemDisappear>() {

		@Override
		public void update(final EventItemDisappear event, EventEmitter<EventItemDisappear> emitter) {
			Actor actor = obtainItemActor(
				event.x,
				event.y,
				event.item
			);
			if (gameScreen.getConfig().animationsEnabled) {
				AlphaAction alphaAction = new AlphaAction();
				alphaAction.setAlpha(0.0f);
				alphaAction.setDuration(0.1f);
				Action sequence = sequence(alphaAction, run(new Runnable() {
					@Override
					public void run() {
						removeItemActor(event.item);
						eventProcessor.signalEventProcessingDone();
					}
				}));
				actor.addAction(sequence);
				addActor(actor);
			} else {
				removeItemActor(event.item);
				eventProcessor.signalEventProcessingDone();
			}
		}
	}, EventItemDisappear.class);
	model.subscribe(new Observer<EventSound>() {

		@Override
		public void update(EventSound event, EventEmitter<EventSound> emitter) {
			Actor actor = obtainSoundActor(
				event.sound,
				event.x,
				event.y
			);
			if (event.source == null) {
				messageLog.pushMessage(
					Languages.getText("events.sound_from_cell", event.sound)
				);
			} else if (event.source == player) {
				messageLog.pushMessage(
					Languages.getText("events.sound_from_player", event.sound)
				);
			} else {
				messageLog.pushMessage(
					Languages.getText("events.sound_from_source", event.source, event.sound)
				);
			}
			addActor(actor);
		}
	}, EventSound.class);
	model.subscribe(new Observer<EventGetDamage>() {
		@Override
		public void update(EventGetDamage event, EventEmitter<EventGetDamage> emitter) {
			messageLog.pushMessage(
				Languages.getText("log.get_damage", event.damageSource, event.damageType, event.character)
			);
			final Actor blood = new Blood(event.character.getX(), event.character.getY());
			blood.addAction(sequence(delay(0.3f), run(new Runnable() {
				@Override
				public void run() {
					getRoot().removeActor(blood);
				}
			})));
			addActor(blood);
			emitter.done(this);
		}
	}, EventGetDamage.class);
	model.subscribe(new Observer<EventDie>() {
		@Override
		public void update(EventDie event, EventEmitter<EventDie> emitter) {
			CharacterActor characterActor = getCharacterActor(event.character);
			getRoot().removeActor(characterActor);
			messageLog.pushMessage(
				Languages.getText("log.death", event.character)
			);
		}
	}, EventDie.class);
	model.subscribe(new Observer<EventAttack>() {
		@Override
		public void update(EventAttack event, EventEmitter<EventAttack> emitter) {

			CharacterActor characterActor = getCharacterActor(event.attacker);
			float dx = event.aim.getX() - event.attacker.getX();
			float dy = event.aim.getY() - event.attacker.getY();
			characterActor.addAction(sequence(
				moveBy(-dx * 0.2f, -dy * 0.2f, 0.1f),
				moveBy(dx * 0.7f, dy * 0.7f, 0.1f),
				run(new Runnable() {
					@Override
					public void run() {
						eventProcessor.signalEventProcessingDone();
					}
				}),
				moveBy(-dx * 0.5f, -dy * 0.5f, 0.2f)
			));
		}

	}, EventAttack.class);
	model.subscribe(new Observer<EventProjectileFly>() {

		@Override
		public void update(EventProjectileFly event, EventEmitter<EventProjectileFly> emitter) {
			Actor actor = obtainFlyingProjectileActor(
				event.item,
				event.fromX,
				event.fromY,
				event.toX,
				event.toY,
				event.style
			);
			addActor(actor);
		}
	}, EventProjectileFly.class);
	model.subscribe(new Observer<EventMoveToPlane>() {

		@Override
		public void update(EventMoveToPlane event, EventEmitter<EventMoveToPlane> emitter) {
			removeActorsOfPlane(gameScreen.getCurrentBackendPlane().getLevel());
			for (RenderCell cell : event.seenCells) {
				if (gameScreen.getCurrentBackendPlane().hasWall(cell.x, cell.y)) {
					if (!hasWallActor(cell.x, cell.y)) {
						addWallActor(cell.x, cell.y);
					}
				} else if (gameScreen.getCurrentBackendPlane().hasObject(cell.x, cell.y)) {
					addObjectActor(cell.x, cell.y);
				}
			}
		}
	}, EventMoveToPlane.class);

}

private void initializeActors() {
	TimeStream timeStream = gameScreen.backendWorld.getPlayer().getTimeStream();
	for (Character character : timeStream.getCharacters()) {
		CharacterActor actor = createCharacterActor(character);
		characterActors.put(character, actor);
		if (character.isPlayer()) {
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
	CharacterActor actor = characterActorFactory.create(character);
	if (playerSeer.canSee(character.getX(), character.getY())) {
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
 * Returns an existing ItemActor for a {@link org.tendiwa.core.RememberedItem}, or creates a new ItemActor for a
 * RememberedItem and returns it.
 *
 * @param item
 * 	A remembered item.
 * @return An existing or a new ItemActor.
 */
public Actor obtainItemActor(int x, int y, Item item) {
	return new ItemActor(x, y, item, renderWorld.getCurrentPlane());
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
 * @return A new ItemActor with MoveToAction and call to {@link EventProcessor#signalEventProcessingDone()} added to
 *         it.
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
		actor = new ProjectileActor(item, fromX, fromY, toX, toY, renderWorld.getCurrentPlane());
	}
	MoveToAction moveToAction = new MoveToAction();
	moveToAction.setPosition(toX, toY);
	moveToAction.setDuration((float) (EnhancedPoint.distanceDouble(fromX, fromY, toX, toY) * 0.05));
	ParallelAction movingAndRotating = parallel(moveToAction, rotateBy(360, moveToAction.getDuration()));
	RunnableAction runnable = run(new Runnable() {
		@Override
		public void run() {
			TendiwaStage.this.getRoot().removeActor(actor);
			eventProcessor.signalEventProcessingDone();
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
	final SoundActor actor = soundActorFactory.create(soundType, x, y);
	return actor;
}

public void updateCharactersVisibility() {
	for (CharacterActor actor : characterActors.values()) {
		if (actor != null) {
			Character character = actor.getCharacter();
			if (playerSeer.canSee(character.getX(), character.getY())
				&& !actor.isVisible()) {
				actor.setVisible(true);
			} else if (!playerSeer.canSee(character.getX(), character.getY())
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
	ObjectActor actor = new ObjectActor(x, y, gameScreen.getCurrentBackendPlane().getGameObject(x, y), renderWorld.getCurrentPlane());
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
	assert border.getObject() != null;
	BorderObjectActor actor = new BorderObjectActor(
		border,
		border.getObject(),
		renderWorld.getCurrentPlane()
	);
	borderObjectActors.put(Chunk.cellHash(border.getX(), border.getY(), Tendiwa.getWorldHeight()), border.getSide(), actor);
	addActor(actor);
	return actor;
}

public void removeBorderObjectActor(int worldX, int worldY, CardinalDirection side) {
	BorderObjectActor removedActor = borderObjectActors.remove(Chunk.cellHash(worldX, worldY, Tendiwa.getWorldHeight()), side);
	getRoot().removeActor(removedActor);
}

public MarkersRegistry getMarkersRegistry() {
	return markersRegistry;
}
}
