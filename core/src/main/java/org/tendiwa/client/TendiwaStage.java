package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.rendering.effects.Blood;
import org.tendiwa.client.ui.factories.*;
import org.tendiwa.client.ui.model.MessageLog;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.clients.RenderBorder;
import org.tendiwa.core.clients.RenderCell;
import org.tendiwa.core.clients.RenderWorld;
import org.tendiwa.core.events.*;
import org.tendiwa.core.observation.Finishable;
import org.tendiwa.core.observation.Observer;
import org.tendiwa.core.observation.ThreadProxy;
import org.tendiwa.core.player.SinglePlayerMode;
import org.tendiwa.core.vision.Seer;
import org.tendiwa.geometry.Cells;

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
private final Character player;
private final RenderWorld renderWorld;
private final SoundActorFactory soundActorFactory;
private final Seer playerSeer;
private final BorderObjectActorFactory borderObjectActorFactory;
private final ItemActorFactory itemActorFactory;
private final ProjectileActorFactory projectileActorFactory;
private final ObjectActorFactory objectActorFactory;
private final GraphicsConfig config;
private final SinglePlayerMode singlePlayerMode;
private final TimeStream timeStream;
private final World world;
private final WallActorFactory wallActorFactory;
private final CharacterActorFactory characterActorFactory;
private Map<Character, CharacterActor> characterActors = new HashMap<>();
private com.badlogic.gdx.scenes.scene2d.Actor playerCharacterActor;
private Map<Item, Actor> itemActors = new HashMap<>();
private HashMap<Integer, WallActor> wallActors = new HashMap<>();
private Multimap<Integer, Actor> plane2actors = HashMultimap.create();
private Table<Integer, CardinalDirection, BorderObjectActor> borderObjectActors = HashBasedTable.create();

@Inject
TendiwaStage(
	@Named("player") TimeStream timeStream,
	@Named("game_screen_batch") Batch batch,
	@Named("current_player_world") final World world,
	WallActorFactory wallActorFactory,
	CharacterActorFactory characterActorFactory,
	final MessageLog messageLog,
	@Named("player") final Character player,
	final ThreadProxy model,
	final RenderWorld renderWorld,
	GameScreenViewport viewport,
	SoundActorFactory soundActorFactory,
	@Named("player_seer") Seer playerSeer,
	BorderObjectActorFactory borderObjectActorFactory,
	final ItemActorFactory itemActorFactory,
	final ProjectileActorFactory projectileActorFactory,
	final ObjectActorFactory objectActorFactory,
	final BorderMarkerFactory borderMarkerFactory,
	final GraphicsConfig config,
	SinglePlayerMode singlePlayerMode
) {
	super(new FitViewport(world.getWidth(), world.getHeight(), viewport.getCamera()), batch);
	this.timeStream = timeStream;
	this.world = world;
	this.wallActorFactory = wallActorFactory;
	this.characterActorFactory = characterActorFactory;
	this.player = player;
	this.renderWorld = renderWorld;
	this.soundActorFactory = soundActorFactory;
	this.playerSeer = playerSeer;
	this.borderObjectActorFactory = borderObjectActorFactory;
	this.itemActorFactory = itemActorFactory;
	this.projectileActorFactory = projectileActorFactory;
	this.objectActorFactory = objectActorFactory;
	this.config = config;
	this.singlePlayerMode = singlePlayerMode;
//	setCamera(viewport.getCamera());
	initializeActors();
	model.subscribe(new Observer<EventFovChange>() {

		@Override
		public void update(EventFovChange event, Finishable<EventFovChange> emitter) {
			for (Border border : event.seenBorders) {
				borderMarkerFactory.create(border);
			}
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
		public void update(EventMove event, final Finishable<EventMove> emitter) {
			Actor characterActor = getCharacterActor(event.character);
			int index = event.character.y() * world.getWidth() + event.character.x();
			sortActorsByY();

			if (config.animationsEnabled) {
				Action action;
				if (event.movingStyle == MovingStyle.STEP) {
					action = new MoveToAction();
					((MoveToAction) action).setPosition(event.character.x(), event.character.y());
					((MoveToAction) action).setDuration(0.1f);
				} else if (event.movingStyle == MovingStyle.LEAP) {
					MoveByAction moveTo = new MoveByAction();
					moveTo.setAmount(event.character.x() - event.xPrev, event.character.y() - event.yPrev);
					float lengthMovingDuration = 0.3f;
					moveTo.setDuration(lengthMovingDuration);
					MoveByAction moveUp = moveBy(0, -1, lengthMovingDuration / 2);
					moveUp.setInterpolation(Interpolation.exp5Out);
					MoveByAction moveDown = moveBy(0, 1, lengthMovingDuration / 2);
					moveDown.setInterpolation(Interpolation.exp5In);
					Action upAndDown = sequence(moveUp, moveDown);
					action = parallel(moveTo, upAndDown);
				} else {
					action = moveTo(event.character.x(), event.character.y(), 0.1f);
				}
				final Observer<EventMove> observer = this;
				Action sequence = sequence(action, run(new Runnable() {
					@Override
					public void run() {
						updateCharactersVisibility();
						emitter.done(observer);
					}
				}));
				characterActor.addAction(sequence);
			} else {
				characterActor.setX(event.character.x());
				characterActor.setY(event.character.y());
				updateCharactersVisibility();
//				if (event.character.isPlayer()) {
				// If this is player moving, then the next event will be
				// EventFovChange, and to prevent flickering we make the current event
				// render in the same frame as the previous event.
//				}
				emitter.done(this);
				model.waitForNextEventInCurrentFrame();
			}
		}
	}, EventMove.class);
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			for (RenderCell cell : event.seenCells) {
//				renderWorld.getCurrentPlane().seeCell(cell);
				HorizontalPlane plane = player.getPlane();
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
			emitter.done(this);
		}
	}, EventInitialTerrain.class);
	model.subscribe(new Observer<EventItemDisappear>() {

		@Override
		public void update(final EventItemDisappear event, Finishable<EventItemDisappear> emitter) {
			Actor actor = itemActorFactory.create(
				event.x,
				event.y,
				event.item,
				renderWorld.getCurrentPlane()
			);
			if (config.animationsEnabled) {
				AlphaAction alphaAction = new AlphaAction();
				alphaAction.setAlpha(0.0f);
				alphaAction.setDuration(0.1f);
				Action sequence = sequence(alphaAction, run(new Runnable() {
					@Override
					public void run() {
						removeItemActor(event.item);
					}
				}));
				actor.addAction(sequence);
				addActor(actor);
			} else {
				removeItemActor(event.item);
			}
			emitter.done(this);
		}
	}, EventItemDisappear.class);
	model.subscribe(new Observer<EventSound>() {
		@Override
		public void update(EventSound event, final Finishable<EventSound> emitter) {
			final Actor actor = obtainSoundActor(
				event.sound,
				event.x,
				event.y
			);
			final Observer<EventSound> observer = this;
			actor.addAction(sequence(rotateBy(90, 0.3f), run(new Runnable() {
				@Override
				public void run() {
					actor.getParent().removeActor(actor);
					emitter.done(observer);
				}
			})));
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
		public void update(EventGetDamage event, Finishable<EventGetDamage> emitter) {
			messageLog.pushMessage(
				Languages.getText("log.get_damage", event.damageSource, event.damageType, event.character)
			);
			final Actor blood = new Blood(event.character.x(), event.character.y());
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
		public void update(EventDie event, Finishable<EventDie> emitter) {
			CharacterActor characterActor = getCharacterActor(event.character);
			getRoot().removeActor(characterActor);
			messageLog.pushMessage(
				Languages.getText("log.death", event.character)
			);
		}
	}, EventDie.class);
	model.subscribe(new Observer<EventAttack>() {
		@Override
		public void update(EventAttack event, final Finishable<EventAttack> emitter) {
			CharacterActor characterActor = getCharacterActor(event.attacker);
			float dx = event.aim.x() - event.attacker.x();
			float dy = event.aim.y() - event.attacker.y();
			final Observer<EventAttack> self = this;
			characterActor.addAction(sequence(
				moveBy(-dx * 0.2f, -dy * 0.2f, 0.1f),
				moveBy(dx * 0.7f, dy * 0.7f, 0.1f),
				run(new Runnable() {
					@Override
					public void run() {
						emitter.done(self);
					}
				}),
				moveBy(-dx * 0.5f, -dy * 0.5f, 0.2f)
			));
		}

	}, EventAttack.class);
	model.subscribe(new Observer<EventProjectileFly>() {

		@Override
		public void update(EventProjectileFly event, final Finishable<EventProjectileFly> emitter) {
			final Observer<EventProjectileFly> thisObserver = this;
			Actor actor = obtainFlyingProjectileActor(
				event.item,
				event.fromX,
				event.fromY,
				event.toX,
				event.toY,
				event.style,
				new Runnable() {
					@Override
					public void run() {
						emitter.done(thisObserver);
					}
				}
			);
			addActor(actor);
		}
	}, EventProjectileFly.class);
	model.subscribe(new Observer<EventMoveToPlane>() {

		@Override
		public void update(EventMoveToPlane event, Finishable<EventMoveToPlane> emitter) {
			removeActorsOfPlane(player.getPlane().getLevel());
			for (RenderCell cell : event.seenCells) {
				if (player.getPlane().hasWall(cell.x, cell.y)) {
					if (!hasWallActor(cell.x, cell.y)) {
						addWallActor(cell.x, cell.y);
					}
				} else if (player.getPlane().hasObject(cell.x, cell.y)) {
					addObjectActor(cell.x, cell.y);
				}
			}
			emitter.done(this);
		}
	}, EventMoveToPlane.class);
}

private void initializeActors() {
	for (Character character : timeStream.getCharacters()) {
		CharacterActor actor = createCharacterActor(character);
		characterActors.put(character, actor);
		if (singlePlayerMode.isPlayer(character)) {
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
	if (playerSeer.canSee(character.x(), character.y())) {
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

public Actor getPlayerCharacterActor() {
	return playerCharacterActor;
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
 * @return A new ItemActor with MoveToAction added to it.
 */
public Actor obtainFlyingProjectileActor(
	final Projectile item,
	int fromX,
	int fromY,
	int toX,
	int toY,
	EventProjectileFly.FlightStyle style,
	final Runnable onComplete
) {
	final Actor actor;

	Action action;
	boolean rotating = false;
	if (style == EventProjectileFly.FlightStyle.CAST && item instanceof Item) {
		actor = itemActorFactory.create(fromX, fromY, (Item) item, renderWorld.getCurrentPlane());
		rotating = true;
	} else if (item instanceof SpellProjectile) {
		actor = new SpellProjectileFireballActor(fromX, fromY);
	} else {
		actor = projectileActorFactory.create(item, fromX, fromY, toX, toY, renderWorld.getCurrentPlane());
	}
	MoveToAction moveToAction = new MoveToAction();
	moveToAction.setPosition(toX, toY);
	moveToAction.setDuration((float) (Cells.distanceDouble(fromX, fromY, toX, toY) * 0.05));
	ParallelAction movingAndRotating = parallel(moveToAction, rotateBy(360, moveToAction.getDuration()));
	RunnableAction runnable = run(new Runnable() {
		@Override
		public void run() {
			TendiwaStage.this.getRoot().removeActor(actor);
			onComplete.run();
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
	return soundActorFactory.create(soundType, x, y);
}

public void updateCharactersVisibility() {
	for (CharacterActor actor : characterActors.values()) {
		if (actor != null) {
			Character character = actor.getCharacter();
			if (playerSeer.canSee(character.x(), character.y())
				&& !actor.isVisible()) {
				actor.setVisible(true);
			} else if (!playerSeer.canSee(character.x(), character.y())
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
	GameObject gameObject = player.getPlane().getGameObject(x, y);
	WallActor actor = wallActorFactory.create(x, y, (WallType) gameObject, renderWorld.getCurrentPlane());
	wallActors.put(getWallActorKey(x, y), actor);
//	actor.setVisible(false);
	addActor(actor);
	actor.setZIndex(y * world.getWidth() + x);
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
	return x * world.getHeight() + y;
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
	ObjectActor actor = objectActorFactory.create(
		x,
		y,
		player.getPlane().getGameObject(x, y),
		renderWorld.getCurrentPlane()
	);
	plane2actors.put(player.getPlane().getLevel(), actor);
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
	BorderObjectActor actor = borderObjectActorFactory.create(
		border,
		border.getObject(),
		renderWorld.getCurrentPlane()
	);
	borderObjectActors.put(Chunk.cellHash(border.getX(), border.getY(), world.getHeight()), border.getSide(), actor);
	addActor(actor);
	return actor;
}

public void removeBorderObjectActor(int worldX, int worldY, CardinalDirection side) {
	BorderObjectActor removedActor = borderObjectActors.remove(Chunk.cellHash(worldX, worldY, world.getHeight()), side);
	getRoot().removeActor(removedActor);
}

}
