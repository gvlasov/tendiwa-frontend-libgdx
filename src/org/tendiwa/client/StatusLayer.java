package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class StatusLayer {
private final GameScreen gameScreen;
private final BitmapFont font = TendiwaFonts.default20Flipped;

public StatusLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
}

public void draw() {
	gameScreen.batch.begin();
	font.draw(
		gameScreen.batch,
		Gdx.graphics.getFramesPerSecond() + " FPS",
		gameScreen.startPixelX + 20,
		gameScreen.startPixelY + 20
	);
	font.draw(
		gameScreen.batch,
		"screen at " + gameScreen.startCellX + ":" + gameScreen.startCellY,
		gameScreen.startPixelX + 20,
		gameScreen.startPixelY + 20 + 18
	);
	font.draw(
		gameScreen.batch,
		"cursor at " + gameScreen.getCursor().getWorldX() + ":" + gameScreen.getCursor().getWorldY(),
		gameScreen.startPixelX + 20,
		gameScreen.startPixelY + 20 + 36
	);
	gameScreen.batch.end();
}
}
