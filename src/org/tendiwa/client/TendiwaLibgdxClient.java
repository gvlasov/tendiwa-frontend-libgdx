package org.tendiwa.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.Volition;
import org.tendiwa.core.events.EventSelectPlayerCharacter;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

public class TendiwaLibgdxClient implements TendiwaClient {

private static Injector injector;
private final Volition volition;
private final Observable model;

@Inject
public TendiwaLibgdxClient(Volition volition, Observable model) {
	this.volition = volition;
	this.model = model;
}

public static void main(String[] args) {
	if (args.length > 0 && args[0].equals("atlas")) {
		new ResourcesBuilder().buildResources();
	} else {
		Tendiwa backend = Tendiwa.newBackend();
		injector = Tendiwa.getInjector().createChildInjector(new TendiwaLibgdxModule());
		new TendiwaLibgdxClient(injector.getInstance(Volition.class), backend).startup();
		backend.start();
	}
}

@Override
public void startup() {
	Languages.init();
	injector.getInstance(LwjglApplication.class);
}
}
