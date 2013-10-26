package org.tendiwa.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.tendiwa.client.ResourcesBuilder;
import org.tendiwa.client.TendiwaGame;

public class DesktopStarter {
public static void main(String[] args) {
	if (args[0].equals("buildAtlas")) {
		buildResources();
	} else {
		loadGame();
	}
}

public static void loadGame() {
	LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
	cfg.title = "Title";
	cfg.useGL20 = true;
	cfg.width = 1024;
	cfg.height = 768;
	cfg.resizable = false;
	cfg.vSyncEnabled = false;
//        new LwjglApplication(new BookFun(), cfg);
	new LwjglApplication(new TendiwaGame(cfg), cfg);
}

public static void buildResources() {
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/server/modules/images/floors", "floors");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/server/modules/images/objects", "objects");
}
}