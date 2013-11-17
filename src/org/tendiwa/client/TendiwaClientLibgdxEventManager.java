package org.tendiwa.client;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.tendiwa.events.Event;
import org.tendiwa.events.EventFovChange;
import org.tendiwa.events.EventInitialTerrain;
import org.tendiwa.events.EventMove;
import tendiwa.core.EventSay;
import tendiwa.core.RenderCell;
import tendiwa.core.Tendiwa;
import tendiwa.core.TendiwaClientEventManager;

import java.util.LinkedList;
import java.util.Queue;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * On each received {@link Event} this class creates a {@link EventResult} pending operation and placed it in a queue.
 * Each time the game is rendered, all EventResults are processed inside {@link GameScreen#render(float)}.
 */
public class TendiwaClientLibgdxEventManager implements TendiwaClientEventManager {
private GameScreen gameScreen;
private Queue<EventResult> pendingOperations = new LinkedList<>();
private static boolean animationsEnabled = false;

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
			Actor characterActor = gameScreen.getCharacterActor(e.getCharacter());

			if (animationsEnabled) {
				MoveToAction action = new MoveToAction();
				action.setPosition(e.getX(), e.getY());
				action.setDuration(0.1f);
				Action sequence = sequence(action, run(new Runnable() {
					@Override
					public void run() {
						gameScreen.PLAYER.setX(e.getX());
						gameScreen.PLAYER.setY(e.getY());
						gameScreen.eventProcessingDone();
					}
				}));
				characterActor.addAction(sequence);
			} else {
				characterActor.setX(e.getX());
				characterActor.setY(e.getY());
				gameScreen.PLAYER.setX(e.getX());
				gameScreen.PLAYER.setY(e.getY());
				gameScreen.eventProcessingDone();
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
				gameScreen.cells.get(coord).setVisible(false);
			}
			for (RenderCell cell : eventFovChange.seen) {
				gameScreen.cells.put(cell.getX() * Tendiwa.getWorld().getHeight() + cell.getY(), cell);
			}
			gameScreen.eventProcessingDone();
		}
	});
}

@Override
public void event(final EventInitialTerrain eventInitialTerrain) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			for (RenderCell cell : eventInitialTerrain.seen) {
				int coord = cell.getX() * Tendiwa.getWorld().getHeight() + cell.getY();
				gameScreen.cells.put(coord, cell);
			}
			gameScreen.eventProcessingDone();
		}
	});
}

public Queue<EventResult> getPendingOperations() {
	return pendingOperations;
}
}
