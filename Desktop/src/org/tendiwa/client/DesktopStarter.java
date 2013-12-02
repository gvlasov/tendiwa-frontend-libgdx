package org.tendiwa.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopStarter {
public static void main(String[] args) {
	if (args.length > 0 && args[0].equals("atlas")) {
		buildResources();
	} else {
		loadGame();
	}
}

public static void loadGame() {
	TendiwaGame.getInstance().startup();
}

public static void buildResources() {
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/floors", "floors");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/walls", "walls");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/objects", "objects");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/characters", "characters");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/items", "items");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/ui", "ui");
}
}