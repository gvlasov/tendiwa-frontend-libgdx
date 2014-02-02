package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.google.inject.Inject;
import org.tendiwa.client.rendering.effects.Blood;
import org.tendiwa.client.rendering.markers.BorderMarker;
import org.tendiwa.client.ui.model.MessageLog;
import org.tendiwa.client.ui.widgets.UiHealthBar;
import org.tendiwa.core.*;
import org.tendiwa.core.events.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * On each received {@link org.tendiwa.core.observation.Event} this class creates a {@link EventResult} pending operation and placed
 * it in a queue. Each time the game is rendered, all EventResults are processed inside {@link
 * GameScreen#render(float)}.
 */
public class TendiwaClientLibgdxEventManager implements TendiwaClientEventManager, EventResultProvider {
private final EventProcessor eventProcessor;
private final Game game;
private GameScreen gameScreen;
private EventResult pendingOperation;
private MessageLog messageLog;

@Inject
TendiwaClientLibgdxEventManager(MessageLog messageLog, EventProcessor eventProcessor, GameScreen gameScreen, Game game) {
	this.messageLog = messageLog;
	this.eventProcessor = eventProcessor;
	this.gameScreen = gameScreen;
	this.game = game;
}

private void setPendingOperation(EventResult eventResult) {
	assert pendingOperation == null;
	pendingOperation = eventResult;
}

@Override
public void event(final EventMove e) {
}

@Override
public void event(final EventSay e) {
}

@Override
public void event(final EventFovChange e) {
}

@Override
public void event(final EventInitialTerrain e) {
}

@Override
public void event(final EventItemDisappear eventItemDisappear) {
}

@Override
public void event(final EventGetItem eventGetItem) {
}

@Override
public void event(EventLoseItem eventLoseItem) {
}

@Override
public void event(EventItemAppear eventItemAppear) {
}

@Override
public void event(final EventPutOn eventPutOn) {
}

@Override
public void event(final EventWield eventWield) {
}

@Override
public void event(final EventTakeOff eventTakeOff) {
}

@Override
public void event(final EventUnwield e) {
}

@Override
public void event(final EventProjectileFly e) {
}

@Override
public void event(final EventSound eventSound) {
}

@Override
public void event(final EventExplosion e) {
}

@Override
public void event(final EventGetDamage e) {
}

@Override
public void event(final EventAttack e) {
}

@Override
public void event(final EventDie e) {
}

@Override
public void event(final EventMoveToPlane e) {
}

@Override
public EventResult provideEventResult() {
	assert pendingOperation != null;
	EventResult answer = pendingOperation;
	pendingOperation = null;
	return answer;
}

@Override
public boolean hasResultPending() {
	return pendingOperation != null;
}
}
