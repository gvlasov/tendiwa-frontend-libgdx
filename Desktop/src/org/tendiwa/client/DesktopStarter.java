package org.tendiwa.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.tendiwa.core.Tendiwa;

public class DesktopStarter {
private final TendiwaGame game;
private final ResourcesBuilder resourcesBuilder;

DesktopStarter(TendiwaGame game, ResourcesBuilder resourcesBuilder) {
	this.game = game;
	this.resourcesBuilder = resourcesBuilder;
}

public static void main(String[] args) {
	Injector injector = Guice.createInjector(new TendiwaLibgdxModule());
	if (args.length > 0 && args[0].equals("atlas")) {
		injector.getInstance(ResourcesBuilder.class).buildResources();
	} else {
		injector.getInstance(DesktopStarter.class).loadGame();
	}
}

public void loadGame() {
	game.startup();
	Tendiwa.getLogger().setLevel(org.apache.log4j.Level.DEBUG);
}
}