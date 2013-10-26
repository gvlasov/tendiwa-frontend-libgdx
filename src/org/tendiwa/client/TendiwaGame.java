package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tendiwa.core.World;
import tendiwa.modules.MainModule;

public class TendiwaGame extends Game {

public static int WIDTH = 800;
public static int HEIGHT = 600;
final LwjglApplicationConfiguration cfg;
World world;

public TendiwaGame(LwjglApplicationConfiguration cfg) {
	this.cfg = cfg;
}

@Override
public void create() {
	world = new MainModule().createWorld();
	setScreen(new WorldMapScreen(this));
}

@Override
public void render() {
	super.render();
}
}
