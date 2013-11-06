package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tendiwa.core.RequestInitialTerrain;
import tendiwa.core.Tendiwa;
import tendiwa.core.TendiwaClient;

public class TendiwaGame extends Game implements TendiwaClient {

private static TendiwaGame INSTANCE = new TendiwaGame();
public int WIDTH;
public int HEIGHT;
LwjglApplicationConfiguration cfg;
private TendiwaClientLibgdxEventManager eventManager;
private GameScreen gameScreen;

public TendiwaGame() {

}

public static TendiwaGame getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new TendiwaGame();
	}
	return INSTANCE;
}

@Override
public void create() {
	gameScreen = new GameScreen(this);
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
	cfg.width = 1024;
	cfg.height = 768;
	cfg.resizable = false;
//	cfg.vSyncEnabled = false;
//  new LwjglApplication(new BookFun(), cfg);
	new LwjglApplication(this, cfg);
}

@Override
public TendiwaClientLibgdxEventManager getEventManager() {
	if (eventManager == null) {
		eventManager = new TendiwaClientLibgdxEventManager(gameScreen);
	}
	return eventManager;
}

}
