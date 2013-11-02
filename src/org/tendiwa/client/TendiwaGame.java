package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tendiwa.core.TendiwaClient;
import tendiwa.core.TendiwaClientEventManager;

public class TendiwaGame extends Game implements TendiwaClient {

public static int WIDTH = 800;
public static int HEIGHT = 600;
LwjglApplicationConfiguration cfg;
private GameScreen gameScreen;
private final TendiwaClientEventManager eventManager;

public TendiwaGame() {
	gameScreen = new GameScreen(this);
	eventManager = new TendiwaClientLibgdxEventManager(gameScreen);
}

@Override
public void create() {
	setScreen(gameScreen);
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
public TendiwaClientEventManager getEventManager() {
	return eventManager;
}

}
