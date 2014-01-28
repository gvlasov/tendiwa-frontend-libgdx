package org.tendiwa.client;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class TendiwaLibgdxModule extends AbstractModule {
@Override
protected void configure() {
	bind(TendiwaClientLibgdxEventManager.class).in(Scopes.SINGLETON);
	bind(EventProcessor.class).in(Scopes.SINGLETON);
	bind(TendiwaStage.class).in(Scopes.SINGLETON);
	bind(TaskManager.class).in(Scopes.SINGLETON);
	bind(GameScreen.class).in(Scopes.SINGLETON);
	bind(TendiwaGame.class).in(Scopes.SINGLETON);
	bind(ClientConfig.class).in(Scopes.SINGLETON);
	bind(ResourcesBuilder.class);

}
}
