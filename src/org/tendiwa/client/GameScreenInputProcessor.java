package org.tendiwa.client;

import com.badlogic.gdx.InputProcessor;
import tendiwa.core.*;

import java.util.LinkedList;

import static com.badlogic.gdx.Input.Keys.*;

public class GameScreenInputProcessor implements InputProcessor {
final GameScreen gameScreen;
final PlayerCharacter player;
final World world;
final Cell[][] cells;
private Task currentTask;

GameScreenInputProcessor(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	this.player = Tendiwa.getPlayer();
	this.world = Tendiwa.getWorld();
	this.cells = world.getCellContents();
}

@Override
public boolean keyDown(int keycode) {
	// Process camera movement
	if (gameScreen.isEventProcessingGoing()) {
		return false;
	}
	if (keycode == LEFT) {
		if (gameScreen.startCellX > gameScreen.cameraMoveStep - 1) {
			gameScreen.centerCamera(gameScreen.centerPixelX - GameScreen.TILE_SIZE, gameScreen.centerPixelY);
		}
	}
	if (keycode == RIGHT) {
		if (gameScreen.startCellX < gameScreen.maxStartX) {
			gameScreen.centerCamera(gameScreen.centerPixelX + GameScreen.TILE_SIZE, gameScreen.centerPixelY);
		}
	}
	if (keycode == UP) {
		if (gameScreen.startCellY > gameScreen.cameraMoveStep - 1) {
			gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY - GameScreen.TILE_SIZE);
		}
	}
	if (keycode == DOWN) {
		if (gameScreen.startCellY < gameScreen.maxStartY) {
			gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY + GameScreen.TILE_SIZE);
		}
	}
	// Movement
	if (keycode == H || keycode == NUMPAD_4) {
		if (player.canStepOn(player.getX() - 1, player.getY())) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.W));
		}
	} else if (keycode == L || keycode == NUMPAD_6) {
		if (player.canStepOn(player.getX() + 1, player.getY())) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.E));
		}
	} else if (keycode == J || keycode == NUMPAD_2) {
		if (player.canStepOn(player.getX(), player.getY() + 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.S));
		}
	} else if (keycode == K || keycode == NUMPAD_8) {
		if (player.canStepOn(player.getX(), player.getY() - 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.N));
		}
	} else if (keycode == Y || keycode == NUMPAD_7) {
		if (player.canStepOn(player.getX() - 1, player.getY() - 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NW));
		}
	} else if (keycode == U || keycode == NUMPAD_9) {
		if (player.canStepOn(player.getX() + 1, player.getY() - 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NE));
		}
	} else if (keycode == B || keycode == NUMPAD_1) {
		if (player.canStepOn(player.getX() - 1, player.getY() + 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SW));
		}
	} else if (keycode == N || keycode == NUMPAD_3) {
		if (player.canStepOn(player.getX() + 1, player.getY() + 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SE));
		}
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
	final int cellX = (gameScreen.startPixelX + screenX) / GameScreen.TILE_SIZE;
	final int cellY = (gameScreen.startPixelY + screenY) / GameScreen.TILE_SIZE;
	final LinkedList<EnhancedPoint> path = Paths.getPath(player.getX(), player.getY(), cellX, cellY, player, 100);
	if (path == null) {
		return true;
	}
	currentTask = new Task() {
		@Override
		public boolean ended() {
			return gameScreen.PLAYER.getX() == cellX && gameScreen.PLAYER.getY() == cellY;
		}

		@Override
		public void execute() {
			EnhancedPoint nextStep = path.removeFirst();
			player.move(nextStep.x, nextStep.y);
		}
	};
	return true;
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

public void executeCurrentTask() {
	if (currentTask != null) {
		currentTask.execute();
		if (currentTask.ended()) {
			currentTask = null;
		}
	}
}
}

