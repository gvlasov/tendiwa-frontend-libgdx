package org.tendiwa.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.Volition;

public class TendiwaLibgdxClient implements TendiwaClient {
private final Volition volition;

@Inject
TendiwaLibgdxClient(Volition volition) {
	this.volition = volition;
}

public static void main(String[] args) {
	if (args.length > 0 && args[0].equals("atlas")) {
		new ResourcesBuilder().buildResources();
	} else {
		throw new RuntimeException("Wrong arguments");
	}
}

@Override
public void startup() {
	Injector clientInjector = Guice.createInjector(new TendiwaLibgdxModule());
	Languages.init();
	volition.requestSurroundings();
}
}
