package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.tendiwa.events.*;
import tendiwa.core.*;

import java.util.LinkedList;
import java.util.Queue;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

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
			Actor characterActor = gameScreen.getStage().getCharacterActor(e.getCharacter());
			if (animationsEnabled) {
				MoveToAction action = new MoveToAction();
				action.setPosition(Tendiwa.getPlayer().getX(), Tendiwa.getPlayer().getY());
				action.setDuration(0.1f);
				Action sequence = sequence(action, run(new Runnable() {
					@Override
					public void run() {
						gameScreen.signalEventProcessingDone();
					}
				}));
				characterActor.addAction(sequence);
			} else {
				characterActor.setX(Tendiwa.getPlayer().getX());
				characterActor.setY(Tendiwa.getPlayer().getY());
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
			HorizontalPlane plane = gameScreen.player.getPlane();
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
			ItemActor itemActor = gameScreen.getStage().getItemActor(eventItemDisappear.getItem());
			if (animationsEnabled) {
				AlphaAction alphaAction = new AlphaAction();
				alphaAction.setAlpha(0.0f);
				alphaAction.setDuration(0.4f);
				itemActor.addAction(sequence(alphaAction, run(new Runnable() {
					@Override
					public void run() {
						gameScreen.getStage().removeItemActor(eventItemDisappear.getItem());
						gameScreen.signalEventProcessingDone();
					}
				})));
			} else {
				gameScreen.getStage().removeItemActor(eventItemDisappear.getItem());
				gameScreen.signalEventProcessingDone();
			}
		}
	});
}

@Override
public void event(final EventGetItem eventGetItem) {

}

public Queue<EventResult> getPendingOperations() {
	return pendingOperations;
}
}
