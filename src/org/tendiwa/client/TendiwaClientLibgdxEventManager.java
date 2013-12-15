package org.tendiwa.client;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.tendiwa.events.*;
import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.LinkedList;
import java.util.Queue;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * On each received {@link Event} this class creates a {@link EventResult} pending operation and placed it in a queue.
 * Each time the game is rendered, all EventResults are processed inside {@link GameScreen#render(float)}.
 */
public class TendiwaClientLibgdxEventManager implements TendiwaClientEventManager {
private static boolean animationsEnabled = false;
private GameScreen gameScreen;
private Queue<EventResult> pendingOperations = new LinkedList<>();

TendiwaClientLibgdxEventManager(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
}

public static void toggleAnimations() {
	animationsEnabled = !animationsEnabled;
}

@Override
public void event(final EventMove e) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			com.badlogic.gdx.scenes.scene2d.Actor characterActor = gameScreen.getStage().getCharacterActor(e.character);
			if (animationsEnabled) {
				Action action;
				if (e.movingStyle == MovingStyle.STEP) {
					action = new MoveToAction();
					((MoveToAction) action).setPosition(e.character.getX(), e.character.getY());
					((MoveToAction) action).setDuration(0.1f);
				} else if (e.movingStyle == MovingStyle.LEAP) {
					MoveByAction moveTo = new MoveByAction();
					moveTo.setAmount(e.character.getX() - e.xPrev, e.character.getY() - e.yPrev);
					float lengthMovingDuration = 0.3f;
					moveTo.setDuration(lengthMovingDuration);
					MoveByAction moveUp = moveBy(0, -1, lengthMovingDuration / 2);
					moveUp.setInterpolation(Interpolation.exp5Out);
					MoveByAction moveDown = moveBy(0, 1, lengthMovingDuration / 2);
					moveDown.setInterpolation(Interpolation.exp5In);
					Action upAndDown = sequence(moveUp, moveDown);
					action = parallel(moveTo, upAndDown);
				} else {
					action = moveTo(e.character.getX(), e.character.getY(), 0.1f);
				}
				Action sequence = sequence(action, run(new Runnable() {
					@Override
					public void run() {
						gameScreen.getStage().updateCharactersVisibility();
						gameScreen.signalEventProcessingDone();
					}
				}));
				characterActor.addAction(sequence);
			} else {
				characterActor.setX(e.character.getX());
				characterActor.setY(e.character.getY());
				gameScreen.getStage().updateCharactersVisibility();
				gameScreen.signalEventProcessingDone();
			}
		}
	});
}

@Override
public void event(final EventSay e) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {

		}
	});
}

@Override
public void event(final EventFovChange eventFovChange) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			for (Integer coord : eventFovChange.unseen) {
				RenderCell cell = GameScreen.getRenderWorld().getCell(coord);
				cell.setVisible(false);
				if (gameScreen.player.getPlane().hasAnyItems(cell.x, cell.y)) {
					for (Item item : gameScreen.player.getPlane().getItems(cell.x, cell.y)) {
						gameScreen.renderWorld.addUnseenItem(cell.x, cell.y, item);
					}
				}
			}
			for (RenderCell cell : eventFovChange.seen) {
				GameScreen.getRenderWorld().seeCell(cell);
				if (gameScreen.renderWorld.hasAnyUnseenItems(cell.x, cell.y)) {
					gameScreen.renderWorld.removeUnseenItems(cell.x, cell.y);
				}
			}
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(final EventInitialTerrain eventInitialTerrain) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			for (RenderCell cell : eventInitialTerrain.seen) {
				GameScreen.getRenderWorld().seeCell(cell);
			}
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(final EventItemDisappear eventItemDisappear) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			Actor actor = gameScreen.getStage().obtainItemActor(
				eventItemDisappear.x,
				eventItemDisappear.y,
				eventItemDisappear.item
			);
			if (animationsEnabled) {
				AlphaAction alphaAction = new AlphaAction();
				alphaAction.setAlpha(0.0f);
				alphaAction.setDuration(0.1f);
				Action sequence = sequence(alphaAction, run(new Runnable() {
					@Override
					public void run() {
						gameScreen.getStage().removeItemActor(eventItemDisappear.item);
						gameScreen.signalEventProcessingDone();
					}
				}));
				actor.addAction(sequence);
				gameScreen.getStage().addActor(actor);
			} else {
				gameScreen.getStage().removeItemActor(eventItemDisappear.item);
				gameScreen.signalEventProcessingDone();
			}
		}
	});
}

@Override
public void event(final EventGetItem eventGetItem) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			TendiwaUiStage.getInventory().update();
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(EventLoseItem eventLoseItem) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			TendiwaUiStage.getInventory().update();
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(EventItemAppear eventItemAppear) {
}

@Override
public void event(final EventPutOn eventPutOn) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			if (eventPutOn.getCharacter().isPlayer()) {
				TendiwaUiStage.getInventory().update();
			}
			if (eventPutOn.getCharacter().getType().hasAspect(CharacterAspect.HUMANOID)) {
				gameScreen.getStage().getCharacterActor(eventPutOn.getCharacter()).updateTexture();
			}
			gameScreen.signalEventProcessingDone();
		}
	});
}

public Queue<EventResult> getPendingOperations() {
	return pendingOperations;
}

@Override
public void event(final EventWield eventWield) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			if (eventWield.getCharacter().isPlayer()) {
				TendiwaUiStage.getInventory().update();
			}
			if (eventWield.getCharacter().getType().hasAspect(CharacterAspect.HUMANOID)) {
				gameScreen.getStage().getCharacterActor(eventWield.getCharacter()).updateTexture();
			}
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(final EventTakeOff eventTakeOff) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			if (eventTakeOff.getCharacter().isPlayer()) {
				TendiwaUiStage.getInventory().update();
			}
			if (eventTakeOff.getCharacter().getType().hasAspect(CharacterAspect.HUMANOID)) {
				gameScreen.getStage().getCharacterActor(eventTakeOff.getCharacter()).updateTexture();
			}
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(final EventUnwield eventUnwield) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			if (eventUnwield.getCharacter().isPlayer()) {
				TendiwaUiStage.getInventory().update();
			}
			if (eventUnwield.getCharacter().getType().hasAspect(CharacterAspect.HUMANOID)) {
				gameScreen.getStage().getCharacterActor(eventUnwield.getCharacter()).updateTexture();
			}
			gameScreen.signalEventProcessingDone();
		}
	});
}

@Override
public void event(final EventProjectileFly eventProjectileFly) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			com.badlogic.gdx.scenes.scene2d.Actor actor = gameScreen.getStage().obtainFlyingProjectileActor(
				eventProjectileFly.item,
				eventProjectileFly.fromX,
				eventProjectileFly.fromY,
				eventProjectileFly.toX,
				eventProjectileFly.toY,
				eventProjectileFly.style
			);
			gameScreen.getStage().addActor(actor);
		}
	});
}

@Override
public void event(final EventSound eventSound) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			com.badlogic.gdx.scenes.scene2d.Actor actor = gameScreen.getStage().obtainSoundActor(
				eventSound.sound,
				eventSound.x,
				eventSound.y
			);
			gameScreen.getStage().addActor(actor);
		}
	});
}

@Override
public void event(final EventExplosion e) {

	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
//			com.badlogic.gdx.scenes.scene2d.Actor explosionActor = gameScreen.getStage().obtainExplosionActor(
//				e.x,
//				e.y
//			);
//			gameScreen.getStage().addActor(explosionActor);
			gameScreen.signalEventProcessingDone();
		}
	});
}
}
