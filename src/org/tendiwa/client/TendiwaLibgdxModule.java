package org.tendiwa.client;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.bitfire.postprocessing.PostProcessor;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.log4j.Logger;
import org.tendiwa.client.ui.actors.CellSelectionActor;
import org.tendiwa.client.ui.actors.CellSelectionPlainActor;
import org.tendiwa.client.ui.factories.*;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.dependencies.WorldProvider;
import org.tendiwa.core.factories.RenderPlaneFactory;
import org.tendiwa.core.meta.CellPosition;

public class TendiwaLibgdxModule extends AbstractModule {

@Override
protected void configure() {
	bind(TendiwaStage.class).in(Scopes.SINGLETON);
	bind(TaskManager.class).in(Scopes.SINGLETON);
	bind(GameScreen.class).in(Scopes.SINGLETON);
	bind(Game.class).to(TendiwaLibgdxClient.class).in(Scopes.SINGLETON);
	bind(CursorPosition.class).in(Scopes.SINGLETON);
	bind(ResourcesBuilder.class);
	bind(WidgetsPlacer.class).to(SuseikaWidgetsPlacer.class);
	bind(LwjglApplicationConfiguration.class).toInstance(ConfigManager.getLwjglConfiguration());
	bind(GraphicsConfig.class).toInstance(ConfigManager.getGraphicsConfig());
	bind(ApplicationListener.class).to(ScreensSwitcher.class).in(Scopes.SINGLETON);
	bind(FontRegistry.class).in(Scopes.SINGLETON);
	bind(CellSelectionActor.class).to(CellSelectionPlainActor.class).in(Scopes.SINGLETON);
	bind(GameScreenViewport.class).in(Scopes.SINGLETON);
	bind(WorldProvider.class).in(Scopes.SINGLETON);
	bind(InputProcessor.class)
		.annotatedWith(Names.named("default"))
		.to(GameScreenInputProcessor.class);
	bind(Batch.class)
		.annotatedWith(Names.named("game_screen_batch"))
		.to(SpriteBatch.class);
	bind(Logger.class)
		.toInstance(Logger.getLogger("org/tendiwa"));
	bind(CellPosition.class)
		.annotatedWith(Names.named("player"))
		.toProvider(PlayerCharacterProvider.class);
	bind(FreeTypeFontGenerator.class)
		.toProvider(FreeTypeFontGeneratorProvider.class)
		.in(Scopes.SINGLETON);
	bind(FrameBuffer.class)
		.annotatedWith(Names.named("game_screen_depth_test_fb"))
		.toProvider(DepthTestFramebufferProvider.class);
	bind(PostProcessor.class)
		.annotatedWith(Names.named("game_screen_default_post_processor"))
		.toProvider(PostProcessorProvider.class);
	bind(TileTextureRegionProvider.class)
		.annotatedWith(Names.named("transitions"))
		.toProvider(TransitionsTileTextureRegionProvider.class)
		.in(Scopes.SINGLETON);
	bind(Input.class)
		.toProvider(InputProvider.class)
		.in(Scopes.SINGLETON);
	install(new FactoryModuleBuilder()
		.build(BorderObjectActorFactory.class));
	install(new FactoryModuleBuilder()
		.build(ItemActorFactory.class));
	install(new FactoryModuleBuilder()
		.build(ObjectActorFactory.class));
	install(new FactoryModuleBuilder()
		.build(ProjectileActorFactory.class));
	install(new FactoryModuleBuilder()
		.build(WallActorFactory.class));
	install(new FactoryModuleBuilder()
		.build(RenderPlaneFactory.class));
	install(new FactoryModuleBuilder()
		.build(TileTextureRegionProviderFactory.class));
	install(new FactoryModuleBuilder()
		.build(TransitionsToFloorFactory.class));
	install(new FactoryModuleBuilder()
		.build(WallImageCacheFactory.class));
	install(new FactoryModuleBuilder()
		.build(BorderMarkerFactory.class));
	install(new FactoryModuleBuilder()
		.build(CellSelectionFactory.class));

	bind(LwjglApplication.class).to(TendiwaApplication.class);
	bind(LwjglApplication.class).to(TendiwaApplication.class);
}

@Provides
@Named("shader_default")
private ShaderProgram provideDefaultShader(ShaderProgramFactory factory) {
	return factory.create(SpriteBatch.createDefaultShader().getFragmentShaderSource());
}

@Provides
@Named("shader_half_transparency")
private ShaderProgram provideHalfTransparencyShader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/fovHalfTransparency.f.glsl").readString());
}

@Provides
@Named("shader_draw_with_rgb_06")
private ShaderProgram provideDrawWithRgb06Shader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/drawWithRGB06.f.glsl").readString());
}

@Provides
@Named("shader_fov_transition")
private ShaderProgram provideFovTransitionShader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/fovTransition.f.glsl").readString());
}

@Provides
@Named("shader_write_opaque_to_depth")
private ShaderProgram provideWriteOpaqueToDepthShader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/writeOpaqueToDepth.f.glsl").readString());
}

@Provides
@Named("shader_draw_opaque_to_depth_05")
private ShaderProgram provideDrawOpaqueToDepth05Shader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/drawOpaqueToDepth05.glsl").readString());
}

@Provides
@Named("shader_draw_with_depth_0")
private ShaderProgram provideDrawWithDepthOShader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/drawWithDepth0.f.glsl").readString());
}

@Provides
@Named("shader_opaque_0_transparent_05_depth")
private ShaderProgram provideOpaque0Transparent05DepthShader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/opaque0transparent05depth.f.glsl").readString());
}

@Provides
@Named("shader_liquid_floor_animate")
private ShaderProgram provideLiquidFloorAnimateShader(ShaderProgramFactory factory) {
	return factory.create(Gdx.files.internal("shaders/liquidFloorAnimate.f.glsl").readString());
}

}

