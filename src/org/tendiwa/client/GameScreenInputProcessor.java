package org.tendiwa.client;

import com.badlogic.gdx.InputProcessor;
import tendiwa.core.Directions;
import tendiwa.core.RequestSay;
import tendiwa.core.RequestWalk;
import tendiwa.core.Tendiwa;

import static com.badlogic.gdx.Input.Keys.*;

public class GameScreenInputProcessor implements InputProcessor {
GameScreen gameScreen;

GameScreenInputProcessor(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
}

@Override
public boolean keyDown(int keycode) {
	// Process camera movement
	if (gameScreen.isEventProcessingGoing()) {
		return false;
	}
	if (keycode == LEFT) {
		if (gameScreen.startCellX > gameScreen.cameraMoveStep - 1) {
			gameScreen.centerCamera(gameScreen.centerPixelX -GameScreen.TILE_SIZE, gameScreen.centerPixelY);
		}
	}
	if (keycode == RIGHT) {
		if (gameScreen.startCellX < gameScreen.maxStartX) {
			gameScreen.centerCamera(gameScreen.centerPixelX +GameScreen.TILE_SIZE, gameScreen.centerPixelY);
		}
	}
	if (keycode == UP) {
		if (gameScreen.startCellY > gameScreen.cameraMoveStep - 1) {
			gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY -GameScreen.TILE_SIZE);
		}
	}
	if (keycode == DOWN) {
		if (gameScreen.startCellY < gameScreen.maxStartY) {
			gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY +GameScreen.TILE_SIZE);
		}
	}
	// Movement with HJKL YUBN
	if (keycode == H) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.W));
	} else if (keycode == L) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.E));
	} else if (keycode == J) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.S));
	} else if (keycode == K) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.N));
	} else if (keycode == Y) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NW));
	} else if (keycode == U) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NE));
	} else if (keycode == B) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SW));
	} else if (keycode == N) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SE));
	}
	// Movement with numpad
	if (keycode == NUMPAD_7) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NW));
	} else if (keycode == NUMPAD_8) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.N));
	} else if (keycode == NUMPAD_9) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NE));
	} else if (keycode == NUMPAD_4) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.W));
	} else if (keycode == NUMPAD_5) {
		Tendiwa.getServer().pushRequest(new RequestSay("I love penisi"));
	} else if (keycode == NUMPAD_6) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.E));
	} else if (keycode == NUMPAD_1) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SW));
	} else if (keycode == NUMPAD_2) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.S));
	} else if (keycode == NUMPAD_3) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SE));
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

