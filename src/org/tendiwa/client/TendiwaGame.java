package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.tendiwa.core.RequestInitialTerrain;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.TendiwaClientEventManager;

public class TendiwaGame extends Game implements TendiwaClient {

private ItemSelectionScreen itemSelectionScreen;
private final GameScreen gameScreen;
private final WorldMapScreen worldMapScreen;
private final ClientConfig config;
private final EventProcessor eventProcessor;
private final TendiwaClientLibgdxEventManager eventManager;
private final LwjglApplicationConfiguration cfg;

public TendiwaGame(EventProcessor eventProcessor, TendiwaClientLibgdxEventManager eventManager, GameScreen gameScreen, WorldMapScreen worldMapScreen, ClientConfig config, LwjglApplicationConfiguration cfg, ItemSelectionScreen itemSelectionScreen) {
	this.itemSelectionScreen = itemSelectionScreen;
	this.eventProcessor = eventProcessor;
	this.eventManager = eventManager;
	this.gameScreen = gameScreen;
	this.worldMapScreen = worldMapScreen;
	this.config = config;
	this.cfg = cfg;
}

public void switchToItemSelectionScreen() {
	setScreen(itemSelectionScreen);
}

public ItemSelectionScreen getItemSelectionScreen() {
	return itemSelectionScreen;
}

public GameScreen getGameScreen() {
	return gameScreen;
}

public void switchToGameScreen() {
	setScreen(gameScreen);
}

public boolean isGameScreenActive() {
	return getScreen() == gameScreen;
}

public void switchToWorldMapScreen() {
	setScreen(worldMapScreen);
}

@Override
public void create() {
	Languages.init();
	setScreen(null);
	Tendiwa.getServer().pushRequest(new RequestInitialTerrain());
	eventProcessor.processEvents();
}

@Override
public void render() {
	super.render();
}

@Override
public void startup() {
	cfg.title = "The Tendiwa Erpoge";
	cfg.useGL20 = true;
	cfg.width = 1024;
	cfg.height = 768;
	cfg.resizable = false;
	cfg.vSyncEnabled = config.vSync;
	cfg.forceExit = true;
	cfg.foregroundFPS = config.limitFps ? 60 : 10000;
	new LwjglApplication(this, cfg);
}

@Override
public TendiwaClientEventManager getEventManager() {
	return eventManager;
}

@Override
public boolean isAnimationCompleted() {
	return !eventManager.hasResultPending() && !eventProcessor.isEventProcessingGoing();
}
}
