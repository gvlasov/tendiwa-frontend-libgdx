package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.TendiwaClientProvider;

public class TendiwaLibgdxClientProvider implements TendiwaClientProvider {

@Override
public TendiwaClient getClient() {
	Injector injector = Guice.createInjector(new TendiwaLibgdxModule());
	return injector.getInstance(TendiwaLibgdxClient.class);
}
}
