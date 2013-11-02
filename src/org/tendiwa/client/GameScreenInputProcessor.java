package org.tendiwa.client;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import tendiwa.core.Directions;
import tendiwa.core.RequestWalk;
import tendiwa.core.Tendiwa;

public class GameScreenInputProcessor implements InputProcessor {
GameScreen gameScreen;

GameScreenInputProcessor(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
}

@Override
public boolean keyDown(int keycode) {
	if (keycode == Input.Keys.H) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.W));
	} else if (keycode == Input.Keys.L) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.E));
	} else if (keycode == Input.Keys.J) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.S));
	} else if (keycode == Input.Keys.K) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.N));
	} else {
		return false;
	}
	return true;
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	return false;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	return false;
}

@Override
public boolean scrolled(int amount) {
	return false;
}
}
