package org.tendiwa.client;

import tendiwa.core.Directions;
import tendiwa.core.RequestWalk;
import tendiwa.core.Tendiwa;

import static com.badlogic.gdx.Input.Keys.*;
public class GameScreenInputProcessor extends TendiwaInputProcessor {

public GameScreenInputProcessor(final GameScreen gameScreen) {
	super(gameScreen);
	putAction(LEFT, new UiAction("actionCameraMoveWest") {
		@Override
		public void act() {
			if (gameScreen.startCellX > gameScreen.cameraMoveStep - 1) {
				gameScreen.centerCamera(gameScreen.centerPixelX - GameScreen.TILE_SIZE, gameScreen.centerPixelY);
			}
		}
	});
	putAction(L, new UiAction("actionCameraMoveEast") {
		@Override
		public void act() {
			if (gameScreen.startCellX < gameScreen.maxStartX) {
				gameScreen.centerCamera(gameScreen.centerPixelX + GameScreen.TILE_SIZE, gameScreen.centerPixelY);
			}
		}
	});
	putAction(UP, new UiAction("actionCameraMoveNorth") {
		@Override
		public void act() {
			if (gameScreen.startCellY > gameScreen.cameraMoveStep - 1) {
				gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY - GameScreen.TILE_SIZE);
			}
		}
	});
	putAction(K, new UiAction("actionCameraMoveSouth") {
		@Override
		public void act() {
			if (gameScreen.startCellY < gameScreen.maxStartY) {
				gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY + GameScreen.TILE_SIZE);
			}
		}
	});
	putAction(H, new UiAction("actionStepWest") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX() - 1, player.getY())) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.W));
			}
		}
	});

}

@Override
public void keyDown(KeyCombination combination) {

}
}
