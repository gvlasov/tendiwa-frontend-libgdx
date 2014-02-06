package org.tendiwa.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.Volition;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.events.EventSelectPlayerCharacter;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

public class TendiwaLibgdxClient extends Game {

private static Injector injector;
private final Observable model;

@Inject
public TendiwaLibgdxClient(@Named("tendiwa") Observable model) {
	this.model = model;
}

public static void main(String[] args) {
	if (args.length > 0 && args[0].equals("atlas")) {
		new ResourcesBuilder().buildResources();
	} else {
		Tendiwa backend = Tendiwa.newBackend();
		injector = Tendiwa.getInjector().createChildInjector(new TendiwaLibgdxModule());
		injector.getInstance(LwjglApplication.class);
		backend.start();
	}
}


@Override
public void create() {

	Languages.init();
	setScreen(injector.getInstance(GameScreen.class));
}
}
