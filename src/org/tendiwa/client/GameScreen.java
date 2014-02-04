package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bitfire.postprocessing.PostProcessor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;
import org.tendiwa.client.ui.actors.CellSelectionPlainActor;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.core.Character;
import org.tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

public static final int TILE_SIZE = 32;
private final Batch batch;
private final TextureAtlas atlasObjects;
private final FloorLayer floorLayer;
private final FloorFieldOfViewLayer floorFieldOfViewLayer;
private final TendiwaStage stage;
private final CellNetLayer cellNetLayer;
private final ItemsLayer itemsLayer;
private final TendiwaUiStage uiStage;
private final InputMultiplexer inputMultiplexer;
private final StatusLayer statusLayer;
private final GameScreenViewport viewport;
private final GraphicsConfig config;
private final CursorPosition cellSelection;
private final PostProcessor postProcessor;
RenderPlane renderPlane;
/**
 * The World object in backend (not always consistent with current animation state, so you shouldn't read from it
 * directly unless absolutely necessary. For listening for changes in the world use {@link
 * org.tendiwa.core.observation.Event}s.
 */
private World backendWorld;
private Character player;
private Map<Integer, GameObject> objects = new HashMap<>();
private HorizontalPlane currentPlane;
private RenderWorld renderWorld;
private Actor cellSelectionActor;
private final Logger logger;

@Inject
public GameScreen(
	@Named("game_screen_default_post_processor") PostProcessor postProcessor,
	@Named("current_player_world") World world,
	@Named("game_screen_batch") Batch batch,
	CellNetLayer cellNetLayer,
	ItemsLayer itemsLayer,
	FloorLayer floorLayer,
	FloorFieldOfViewLayer floorFieldOfViewLayer,
	GameScreenViewport viewport,
	TendiwaUiStage uiStage,
	GraphicsConfig config,
	TendiwaStage stage,
	GameScreenInputProcessor gameScreenInputProcessor,
	CursorPosition cellSelection,
	StatusLayer statusLayer,
	CellSelectionPlainActor cellSelectionPlainActor,
    Logger logger
) {
	this.postProcessor = postProcessor;
	this.backendWorld = world;
	this.cellNetLayer = cellNetLayer;
	this.itemsLayer = itemsLayer;
	this.floorLayer = floorLayer;
	this.floorFieldOfViewLayer = floorFieldOfViewLayer;
	this.viewport = viewport;
	this.config = config;
	this.cellSelection = cellSelection;
	this.cellSelectionActor = cellSelectionPlainActor;
	this.logger = logger;

	atlasObjects = new TextureAtlas(Gdx.files.internal("pack/objects.atlas"), true);

	TransitionPregenerator.initTileTextureRegionProvider(100);

	this.batch = batch;

	this.stage = stage;

	setRenderingMode();

	this.statusLayer = statusLayer;
	this.uiStage = uiStage;
	inputMultiplexer = new InputMultiplexer(uiStage, gameScreenInputProcessor);
	Gdx.input.setInputProcessor(inputMultiplexer);

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
	synchronized (Tendiwa.getLock()) {
		Actor characterActor = stage.getPlayerCharacterActor();
		stage.act(Gdx.graphics.getDeltaTime());
		viewport.centerCamera(
			(int) (characterActor.getX() * TILE_SIZE),
			(int) (characterActor.getY() * TILE_SIZE)
		);

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
			RenderCell cell = renderPlane.getCell(viewport.getStartCellX() + x, viewport.getStartCellY() + y);
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

public EnhancedPoint screenPixelToWorldCell(int screenX, int screenY) {
	return new EnhancedPoint(
		(viewport.getStartPixelX() + screenX) / GameScreen.TILE_SIZE,
		(viewport.getStartPixelY() + screenY) / GameScreen.TILE_SIZE
	);
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
	Gdx.input.setInputProcessor(inputMultiplexer);
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

public void toggleStatusbar() {
	config.fpsCounter = !config.fpsCounter;
}

public InputProcessor getInputProcessor() {
	return inputMultiplexer;
}

public TendiwaUiStage getUiStage() {
	return uiStage;
}

public GraphicsConfig getConfig() {
	return config;
}

}
