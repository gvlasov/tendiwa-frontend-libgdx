package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.tendiwa.client.extensions.std.StdModule;
import org.tendiwa.client.extensions.std.actions.ActionsWidgetModule;
import org.tendiwa.client.extensions.std.spells.SpellsWidgetModule;
import org.tendiwa.core.Tendiwa;

public class TendiwaLibgdxClient extends Game {

private static Injector injector;

@Inject
public TendiwaLibgdxClient() {
}

public static void main(String[] args) {
	if (args.length > 0 && args[0].equals("atlas")) {
		new ResourcesBuilder().buildResources();
	} else {
		Tendiwa backend = Tendiwa.newBackend();
		backend.start();
		injector = Tendiwa.getInjector().createChildInjector(
			new TendiwaLibgdxModule(),
			new ActionsWidgetModule(),
			new StdModule(),
			new SpellsWidgetModule()
		);
		injector.getInstance(LwjglApplication.class);
	}
}

@Override
public void create() {
	Languages.init();
	setScreen(injector.getInstance(GameScreen.class));
}
}
