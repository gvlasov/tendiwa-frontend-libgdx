package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tendiwa.core.RequestInitialTerrain;
import tendiwa.core.Tendiwa;
import tendiwa.core.TendiwaClient;

public class TendiwaGame extends Game implements TendiwaClient {

private static TendiwaGame INSTANCE;
public int WIDTH;
public int HEIGHT;
LwjglApplicationConfiguration cfg;
private TendiwaClientLibgdxEventManager eventManager;
private GameScreen gameScreen;
private WorldMapScreen worldMapScreen;

public TendiwaGame() {
	if (INSTANCE != null) {
		throw new RuntimeException("Attempting to create multiple TendiwaGame instances");
	}
	INSTANCE = this;
}

public static TendiwaGame getInstance() {
	return INSTANCE;
}

public static void switchToWorldMapScreen() {
	if (INSTANCE.worldMapScreen == null) {
		INSTANCE.worldMapScreen = new WorldMapScreen(INSTANCE);
	}
	INSTANCE.setScreen(INSTANCE.worldMapScreen);
}

public static void switchToGameScreen() {
	INSTANCE.setScreen(INSTANCE.gameScreen);
}

public static boolean isGameScreenActive() {
	return INSTANCE.getScreen() == INSTANCE.gameScreen;
}

@Override
public void create() {
	gameScreen = new GameScreen(this);
	eventManager = new TendiwaClientLibgdxEventManager(gameScreen);
	setScreen(gameScreen);
	Tendiwa.getServer().pushRequest(new RequestInitialTerrain());
}

@Override
public void render() {
	super.render();
}

@Override
public void startup() {
	this.cfg = new LwjglApplicationConfiguration();
	cfg.title = "Title";
	cfg.useGL20 = true;
	cfg.width = 1280;
	cfg.height = 768;
	cfg.resizable = false;
	cfg.vSyncEnabled = false;
	cfg.forceExit = true;
	cfg.foregroundFPS = 10000;
//  new LwjglApplication(new BookFun(), cfg);
	new LwjglApplication(this, cfg);
}

@Override
public TendiwaClientLibgdxEventManager getEventManager() {
	return eventManager;
}
}
