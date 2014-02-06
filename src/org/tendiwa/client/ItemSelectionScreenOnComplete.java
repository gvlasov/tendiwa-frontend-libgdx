package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ItemSelectionScreenOnComplete implements Runnable {
private final Input gdxInput;
private final GameScreen gameScreen;
private final Game game;

@Inject
ItemSelectionScreenOnComplete(Input gdxInput, GameScreen gameScreen, Game game) {
	this.gdxInput = gdxInput;
	this.gameScreen = gameScreen;
	this.game = game;
}

@Override
public void run() {
	gdxInput.setInputProcessor(gameScreen.getInputProcessor());
	game.setScreen(gameScreen);
}
}
