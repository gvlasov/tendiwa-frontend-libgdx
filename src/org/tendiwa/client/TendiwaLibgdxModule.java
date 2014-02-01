package org.tendiwa.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.model.CursorPosition;

public class TendiwaLibgdxModule extends AbstractModule {
@Override
protected void configure() {
	bind(TendiwaClientLibgdxEventManager.class).in(Scopes.SINGLETON);
	bind(EventProcessor.class).in(Scopes.SINGLETON);
	bind(TendiwaStage.class).in(Scopes.SINGLETON);
	bind(TaskManager.class).in(Scopes.SINGLETON);
	bind(GameScreen.class).in(Scopes.SINGLETON);
	bind(Game.class).to(ScreensSwitcher.class).in(Scopes.SINGLETON);
	bind(CursorPosition.class).in(Scopes.SINGLETON);
	bind(ResourcesBuilder.class);
	bind(WidgetsPlacer.class).to(SuseikaWidgetsPlacer.class);
	bind(LwjglApplicationConfiguration.class).toInstance(ConfigManager.getLwjglConfiguration());
	bind(GraphicsConfig.class).toInstance(ConfigManager.getGraphicsConfig());
	bind(ApplicationListener.class).to(ScreensSwitcher.class).in(Scopes.SINGLETON);
	bind(FontRegistry.class).in(Scopes.SINGLETON);
	try {
		bind(LwjglApplication.class).toConstructor(LwjglApplication.class.getConstructor(ApplicationListener.class, LwjglApplicationConfiguration.class));
	} catch (NoSuchMethodException e) {
		e.printStackTrace();
	}
}
}
