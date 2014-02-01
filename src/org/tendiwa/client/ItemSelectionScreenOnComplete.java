package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;

public class ItemSelectionScreenOnComplete implements Runnable {
private final GameScreen gameScreen;
private final Game game;

@Inject
ItemSelectionScreenOnComplete(GameScreen gameScreen, Game game) {
	this.gameScreen = gameScreen;
	this.game = game;
}

@Override
public void run() {
	Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
	game.setScreen(gameScreen);
}
}
