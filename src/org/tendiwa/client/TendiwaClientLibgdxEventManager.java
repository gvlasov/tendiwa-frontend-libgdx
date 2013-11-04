package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.tendiwa.events.Event;
import org.tendiwa.events.EventMove;
import tendiwa.core.EventSay;
import tendiwa.core.TendiwaClientEventManager;

import java.util.LinkedList;
import java.util.Queue;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * On each received {@link Event} this class creates a {@link EventResult} pending operation and placed it in a queue.
 * Each time the game is rendered, all EventResults are processed inside {@link GameScreen#render(float)}.
 */
public class TendiwaClientLibgdxEventManager implements TendiwaClientEventManager {
private GameScreen gameScreen;
private Queue<EventResult> pendingOperations = new LinkedList<>();

TendiwaClientLibgdxEventManager(GameScreen gameScreen) {

	this.gameScreen = gameScreen;
}

@Override
public void event(final EventMove e) {
	pendingOperations.add(new EventResult() {
		@Override
		public void process() {
			Actor characterActor = gameScreen.getCharacterActor(e.getCharacter());
			MoveToAction action = new MoveToAction();
			action.setPosition(e.getX(), e.getY());
			action.setDuration(0.1f);
			Action sequence = sequence(action, run(new Runnable() {
				@Override
				public void run() {
					gameScreen.eventProcessingDone();
				}
			}));
			characterActor.addAction(sequence);
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

public Queue<EventResult> getPendingOperations() {
	return pendingOperations;
}
}
