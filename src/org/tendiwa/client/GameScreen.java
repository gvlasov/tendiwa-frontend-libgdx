package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bitfire.postprocessing.PostProcessor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.rendering.markings.MarkingsLayer;
import org.tendiwa.client.ui.TendiwaUiStage;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.*;
import org.tendiwa.core.observation.ThreadProxy;
import org.tendiwa.core.volition.Volition;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

public static final int TILE_SIZE = 32;
private final Batch batch;
private final TextureAtlas atlasObjects;
private final FloorLayer floorLayer;
private final FloorFieldOfViewLayer floorFieldOfViewLayer;
private final TendiwaStage stage;
private final Tendiwa tendiwa;
private final CellNetLayer cellNetLayer;
private final ItemsLayer itemsLayer;
private final TendiwaUiStage uiStage;
private final StatusLayer statusLayer;
private final GameScreenViewport viewport;
private final GraphicsConfig config;
private final CursorPosition cellSelection;
private final RenderWorld renderWorld;
private final Volition volition;
private final MarkingsLayer markings;
private final TaskManager taskManager;
private final Server server;
private final ThreadProxy model;
private final PostProcessor postProcessor;
/**
 * The World object in backend (not always consistent with current animation state, so you shouldn't read from it
 * directly unless absolutely necessary. For listening for changes in the world use {@link
 * org.tendiwa.core.observation.Event}s.
 */
private World backendWorld;
private Map<Integer, GameObject> objects = new HashMap<>();

@Inject
public GameScreen(
	ThreadProxy model,
	@Named("game_screen_default_post_processor") PostProcessor postProcessor,
	@Named("current_player_world") World world,
	@Named("game_screen_batch") Batch batch,
	Tendiwa tendiwa,
	CellNetLayer cellNetLayer,
	ItemsLayer itemsLayer,
	FloorLayer floorLayer,
	FloorFieldOfViewLayer floorFieldOfViewLayer,
	GameScreenViewport viewport,
	TendiwaUiStage uiStage,
	GraphicsConfig config,
	TendiwaStage stage,
	@Named("default") UiMode gameScreenInputProcessor,
	CursorPosition cellSelection,
	StatusLayer statusLayer,
	RenderWorld renderWorld,
	Volition volition,
	UiModeManager uiModeManager,
	MarkingsLayer markings,
	TaskManager taskManager,
	Server server

) {
	this.model = model;
	this.postProcessor = postProcessor;
	this.backendWorld = world;
	this.tendiwa = tendiwa;
	this.cellNetLayer = cellNetLayer;
	this.itemsLayer = itemsLayer;
	this.floorLayer = floorLayer;
	this.floorFieldOfViewLayer = floorFieldOfViewLayer;
	this.viewport = viewport;
	this.config = config;
	this.cellSelection = cellSelection;
	this.renderWorld = renderWorld;
	this.volition = volition;
	this.markings = markings;
	this.taskManager = taskManager;
	this.server = server;

	atlasObjects = new TextureAtlas(Gdx.files.internal("pack/objects.atlas"), true);

	this.batch = batch;

	this.stage = stage;

	setRenderingMode();

	this.statusLayer = statusLayer;
	this.uiStage = uiStage;
	uiModeManager.pushMode(gameScreenInputProcessor);
	volition.requestSurroundings();
	uiStage.buildUi();
}

/**
 * Sets continuous rendering. Needed for restoration of this Screen after switching from another screen with
 * non-continuous rendering.
 */
private void setRenderingMode() {
	Gdx.graphics.setContinuousRendering(true);
	Gdx.graphics.requestRendering();
}

@Override
public void render(float delta) {
	if (model.hasEventsCollected()) {
		model.executeCollected();
	}
	synchronized (tendiwa.getLock()) {
		if (taskManager.hasCurrentTask() && model.areAllEmittersCheckedOut() && !server.hasRequestToProcess()) {
			taskManager.executeCurrentTask();
		}
		Actor characterActor = stage.getPlayerCharacterActor();
		stage.act(Gdx.graphics.getDeltaTime());
		int cameraCenterX = (int) (characterActor.getX() * TILE_SIZE);
		int cameraCenterY = (int) (characterActor.getY() * TILE_SIZE);
		if (viewport.getCenterPixelX() != cameraCenterX || viewport.getCenterPixelY() != cameraCenterY) {
			viewport.centerCamera(cameraCenterX, cameraCenterY);
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.getCamera().update();
		batch.setProjectionMatrix(viewport.getCamera().combined);

		postProcessor.capture();
		floorLayer.draw();
		floorFieldOfViewLayer.draw();
		itemsLayer.draw();
		cellNetLayer.draw();
		drawObjects();
		stage.draw();
		markings.act();
		markings.draw();
		uiStage.act();
		uiStage.draw();
//		Table.drawDebug(uiStage);
		if (config.fpsCounter) {
			statusLayer.draw();
		}
		postProcessor.render();
	}
}

private void drawObjects() {
	batch.begin();
	for (int x = 0; x < viewport.getWindowWidthPixels() / TILE_SIZE + (viewport.getCenterPixelX() == viewport.getMaxPixelX() ? 0 : 1); x++) {
		// Objects are drawn for one additional row to see high objects
		for (int y = 0; y < viewport.getWindowHeightPixels() / TILE_SIZE + (viewport.getCenterPixelY() == viewport.getMaxPixelY() || viewport.getCenterPixelY() == viewport.getMaxPixelY() - TILE_SIZE ? 0 : 2); y++) {
			RenderCell cell = renderWorld.getCurrentPlane().getCell(viewport.getStartCellX() + x, viewport.getStartCellY() + y);
			if (cell != null) {
				// If the frontend has already received this cell from backend
				if (cell.isVisible()) {
					// Draw visible object
					TextureAtlas.AtlasRegion objectTexture = getObjectTextureByCell(viewport.getStartCellX() + x, viewport.getStartCellY() + y);
					if (objectTexture != null) {
						int textureX = (viewport.getStartCellX() + x) * TILE_SIZE - (objectTexture.getRegionWidth() - TILE_SIZE) / 2;
						int textureY = (viewport.getStartCellY() + y) * TILE_SIZE - (objectTexture.getRegionHeight() - TILE_SIZE);
						batch.draw(objectTexture, textureX, textureY);
					}
				}
			}
		}
	}
	batch.end();
}

private TextureAtlas.AtlasRegion getObjectTextureByCell(int x, int y) {
	GameObject gameObject = objects.get(x * backendWorld.getHeight() + y);
	if (gameObject == null) {
		return null;
	}
	return atlasObjects.findRegion(
		gameObject.getType().getResourceName()
	);
}

@Override
public void resize(int width, int height) {

}

@Override
public void show() {
	setRenderingMode();
//	Gdx.input.setInputProcessor(inputMultiplexer);
}

@Override
public void hide() {

}

@Override
public void pause() {

}

@Override
public void resume() {

}

@Override
public void dispose() {

}

public TendiwaUiStage getUiStage() {
	return uiStage;
}

public GraphicsConfig getConfig() {
	return config;
}

}
