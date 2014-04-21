package org.tendiwa.client.extensions.std.itemSelection;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.uiModes.UiModeManager;

@Singleton
public class ItemSelectionScreenOnComplete implements Runnable {
private final Screen gameScreen;
private final Game game;
private final UiModeManager uiModeManager;

@Inject
ItemSelectionScreenOnComplete(
	@Named("game") Screen gameScreen,
	Game game,
    UiModeManager uiModeManager
) {
	this.gameScreen = gameScreen;
	this.game = game;
	this.uiModeManager = uiModeManager;
}

@Override
public void run() {
	uiModeManager.popMode();
	game.setScreen(gameScreen);
}
}
