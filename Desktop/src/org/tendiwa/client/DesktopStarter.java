package org.tendiwa.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;

public class DesktopStarter {
private final TendiwaLibgdxClient client;

DesktopStarter(TendiwaLibgdxClient client) {
	this.client = client;
}

public static void main(String[] args) {
	Injector injector = Guice.createInjector(new TendiwaLibgdxModule());
	if (args.length > 0 && args[0].equals("atlas")) {
		new ResourcesBuilder().buildResources();
	} else {
		injector.getInstance(DesktopStarter.class).loadGame();
	}
}

public void loadGame() {
	client.startup();
	Tendiwa.getLogger().setLevel(org.apache.log4j.Level.DEBUG);
}
}