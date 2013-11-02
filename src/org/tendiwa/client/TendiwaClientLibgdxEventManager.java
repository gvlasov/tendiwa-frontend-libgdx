package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.events.EventMove;
import tendiwa.core.EventSay;
import tendiwa.core.TendiwaClientEventManager;

public class TendiwaClientLibgdxEventManager implements TendiwaClientEventManager {
private GameScreen gameScreen;

TendiwaClientLibgdxEventManager(GameScreen gameScreen) {

	this.gameScreen = gameScreen;
}

@Override
public void event(EventMove e) {
	Actor characterActor = gameScreen.getCharacterActor(e.getCharacter());
	characterActor.setX(e.getX());
	characterActor.setY(e.getY());
//	gameScreen.centerCamera(gameScreen.PLAYER.getX(), gameScreen.PLAYER.getY());
}

@Override
public void event(EventSay eventSay) {
	throw new UnsupportedOperationException();
}
}
