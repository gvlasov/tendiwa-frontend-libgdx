package org.tendiwa.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public final class ConfigManager {
private static final GraphicsConfig graphicsConfig = new GraphicsConfig();

public static LwjglApplicationConfiguration getLwjglConfiguration() {
	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	config.title = "The Tendiwa Erpoge";
	config.useGL20 = true;
	config.width = 1024;
	config.height = 768;
	config.resizable = false;
	config.vSyncEnabled = graphicsConfig.vSync;
	config.forceExit = true;
	config.foregroundFPS = graphicsConfig.limitFps ? 60 : 10000;
	return config;
}

public static GraphicsConfig getGraphicsConfig() {
	return graphicsConfig;
}
}
