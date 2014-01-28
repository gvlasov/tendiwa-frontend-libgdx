package org.tendiwa.client;

import com.badlogic.gdx.Gdx;

public class ItemSelectionScreenOnComplete implements Runnable {
private final TendiwaGame game;

ItemSelectionScreenOnComplete(TendiwaGame game) {
	this.game = game;
}

@Override
public void run() {
	Gdx.input.setInputProcessor(game.getGameScreen().getInputProcessor());
	game.switchToGameScreen();
}
}
