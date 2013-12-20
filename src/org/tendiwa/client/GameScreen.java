package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class GameScreen implements Screen {

static final int TILE_SIZE = 32;
static final ShaderProgram defaultShader = SpriteBatch.createDefaultShader();
static final ShaderProgram drawWithRGB06Shader = GameScreen.createShader(Gdx.files.internal("shaders/drawWithRGB06.f.glsl"));
private static GameScreen INSTANCE;
final int maxStartX;
final int maxStartY;
final SpriteBatch batch;
final int windowHeight;
final int windowWidth;
final int worldWidthCells;
final int worldHeightCells;
final FrameBuffer depthTestFrameBuffer;
final WallsLayer wallsLayer;
final int windowWidthCells;
final int windowHeightCells;
final RenderWorld renderWorld;
private final TendiwaGame game;
private final TextureAtlas atlasObjects;
private final TextureAtlas atlasUi;
private final TendiwaInputProcessor controller;
private final FloorLayer floorLayer;
private final FloorFieldOfViewLayer floorFieldOfViewLayer;
private final TendiwaStage stage;
private final CellNetLayer cellNetLayer;
private final Cursor cursor;
private final ItemsLayer itemsLayer;
private final TendiwaUiStage uiStage;
private final InputMultiplexer inputMultiplexer;
private final Server server;
private final StatusLayer statusLayer;
private final ClientConfig config;
protected int startCellX;
OrthographicCamera camera;
/**
 * The World object in backend (not always consistent with current animation state, so you shouldn't read from it
 * directly unless absolutely necessary. For listening for changes in the world use {@link org.tendiwa.events.Event}s.
 */
World backendWorld;
Character player;
int startCellY;
int centerPixelX;
int centerPixelY;
int cameraMoveStep = 1;
int startPixelX;
int startPixelY;
/**
 * Max index of each floor type's images.
 */
private boolean eventResultProcessingIsGoing = false;
/**
 * The greatest value of camera's center pixel on y axis in world coordinates.
 */
private int maxPixelY;
/**
 * The greatest value of camera's center pixel on x axis in world coordinates.
 */
private int maxPixelX;
private Map<Integer, GameObject> objects = new HashMap<>();

public GameScreen(final TendiwaGame game, ClientConfig config) {
	this.config = config;
	INSTANCE = this;
	this.game = game;
	backendWorld = Tendiwa.getWorld();
	player = Tendiwa.getPlayerCharacter();

	worldWidthCells = Tendiwa.getWorld().getWidth();
	worldHeightCells = Tendiwa.getWorld().getHeight();
	windowWidth = game.cfg.width;
	windowHeight = game.cfg.height;
	windowWidthCells = (int) Math.ceil(((float) windowWidth) / TILE_SIZE);
	windowHeightCells = (int) Math.ceil(((float) windowHeight) / TILE_SIZE);

	camera = new OrthographicCamera(windowWidth, windowHeight);
	camera.setToOrtho(true, windowWidth, windowHeight);
	centerCamera(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE);
	camera.update();

	atlasObjects = new TextureAtlas(Gdx.files.internal("pack/objects.atlas"), true);
	atlasUi = new TextureAtlas(Gdx.files.internal("pack/ui.atlas"), true);

	TransitionPregenerator.initTileTextureRegionProvider(100);

	// Sprite batch for drawing to world.
	batch = new SpriteBatch();

	maxStartX = worldWidthCells - windowWidthCells - cameraMoveStep;
	maxStartY = worldHeightCells - windowHeightCells - cameraMoveStep;
	maxPixelX = backendWorld.getWidth() * TILE_SIZE - windowWidth / 2;
	maxPixelY = backendWorld.getHeight() * TILE_SIZE - windowHeight / 2;

	depthTestFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, windowWidth, windowHeight, true);

	stage = new TendiwaStage(this);

	controller = new TendiwaInputProcessor(this);

	setRenderingMode();

	renderWorld = new RenderWorld(backendWorld);
	wallsLayer = new WallsLayer(this);
	floorLayer = new FloorLayer(this);
	floorFieldOfViewLayer = new FloorFieldOfViewLayer(this);
	cellNetLayer = new CellNetLayer(this);
	itemsLayer = new ItemsLayer(this);
	statusLayer = new StatusLayer(this);
	cursor = new Cursor(this);
	uiStage = new TendiwaUiStage();
	inputMultiplexer = new InputMultiplexer(uiStage, controller);
	Gdx.input.setInputProcessor(inputMultiplexer);
	server = Tendiwa.getServer();

}

public static ShaderProgram createShader(FileHandle file) {
	ShaderProgram shader = new ShaderProgram(defaultShader.getVertexShaderSource(), file.readString());
	if (!shader.isCompiled()) {
		Tendiwa.getLogger().error(shader.getLog());
		throw new RuntimeException("Could not compile a shader");
	}
	return shader;
}

public static RenderWorld getRenderWorld() {
	return INSTANCE.renderWorld;
}

/**
 * Centers camera on a certain point of the world to render viewport around that point. Viewport will always be inside
 * world rectangle, so near world borders camera will be shifted back so viewport doesn't look at area outside the
 * world.
 *
 * @param x
 * 	X coordinate in pixels
 * @param y
 * 	Y coordinate in pixels
 */
void centerCamera(int x, int y) {
	if (x < windowWidth / 2) {
		x = windowWidth / 2;
	}
	if (y < windowHeight / 2) {
		y = windowHeight / 2;
	}
	if (x > maxPixelX) {
		x = maxPixelX;
	}
	if (y > maxPixelY) {
		y = maxPixelY;
	}
	startCellX = x / TILE_SIZE - windowWidthCells / 2 - windowWidthCells % 2;
	startCellY = y / TILE_SIZE - windowHeightCells / 2 - windowHeightCells % 2;
	centerPixelX = x;
	centerPixelY = y;
	startPixelX = centerPixelX - windowWidth / 2;
	startPixelY = centerPixelY - windowHeight / 2;

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
		processEvents();
		stage.act(Gdx.graphics.getDeltaTime());
		centerCamera(
			(int) (characterActor.getX() * TILE_SIZE),
			(int) (characterActor.getY() * TILE_SIZE)
		);

		camera.position.set(centerPixelX, centerPixelY, 0);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		floorLayer.draw();
		floorFieldOfViewLayer.draw();
		wallsLayer.draw();
		itemsLayer.draw();
		cellNetLayer.draw();
		if (CellSelection.getInstance().isActive()) {
			CellSelection.getInstance().draw();
		} else {
			cursor.updateCursorCoords();
			cursor.draw();
		}
		drawObjects();
		stage.draw();
		uiStage.act();
		uiStage.draw();
//		Table.drawDebug(uiStage);
		if (config.statusbarEnabled) {
			statusLayer.draw();
		}
	}
}

private void drawObjects() {
	batch.begin();
	for (int x = 0; x < windowWidth / TILE_SIZE + (centerPixelX == maxPixelX ? 0 : 1); x++) {
		// Objects are drawn for one additional row to see high objects
		for (int y = 0; y < windowHeight / TILE_SIZE + (centerPixelY == maxPixelY || centerPixelY == maxPixelY - TILE_SIZE ? 0 : 2); y++) {
			RenderCell cell = renderWorld.getCell(startCellX + x, startCellY + y);
			if (cell != null) {
				// If the frontend has already received this cell from backend
				if (cell.isVisible()) {
					// Draw visible object
					TextureAtlas.AtlasRegion objectTexture = getObjectTextureByCell(startCellX + x, startCellY + y);
					if (objectTexture != null) {
						int textureX = (startCellX + x) * TILE_SIZE - (objectTexture.getRegionWidth() - TILE_SIZE) / 2;
						int textureY = (startCellY + y) * TILE_SIZE - (objectTexture.getRegionHeight() - TILE_SIZE);
						batch.draw(objectTexture, textureX, textureY);
					}
				}
			}
		}
	}
	// Draw stats
	RenderCell cellUnderCursor = renderWorld.getCell(cursor.getWorldX(), cursor.getWorldY());
	batch.end();
}

/**
 * Checks if a floor tile should be drawn under certain cell.
 *
 * @param x
 * 	World x coordinate of a cell.
 * @param y
 * 	World y coordinate of a cell.
 * @return False if there is a wall in cell {x:y} and cell {x:y+1} is not yet seen, true otherwise.
 */
boolean isFloorUnderWallShouldBeDrawn(int x, int y) {
	return !(renderWorld.getCell(x, y).hasWall() && !renderWorld.hasCell(x, y + 1));
}

int getMaxRenderCellX() {
	return startCellX + windowWidthCells + (startPixelX % TILE_SIZE == 0 ? 0 : 1) + (windowWidthCells % 2 == 0 ? 0 : 1);
}

int getMaxRenderCellY() {
	return startCellY + windowHeightCells + (startPixelY % TILE_SIZE == 0 ? 0 : 1) + (windowHeightCells % 2 == 0 ? 0 : 1);
}

private void processEvents() {
	Queue<EventResult> queue = game.getEventManager().getPendingOperations();
	if (!eventResultProcessingIsGoing && !Server.isTurnComputing() && queue.size() == 0) {
		controller.executeCurrentTask();
	}
	// Loop variable will remain true if it is not set to true inside .process().
	while (!eventResultProcessingIsGoing && !queue.isEmpty()) {
		EventResult result = queue.remove();
		eventResultProcessingIsGoing = true;
		result.process();
	}
}

EnhancedPoint screenPixelToWorldCell(int screenX, int screenY) {
	return new EnhancedPoint(
		(startPixelX + screenX) / GameScreen.TILE_SIZE,
		(startPixelY + screenY) / GameScreen.TILE_SIZE
	);
}

void signalEventProcessingDone() {
	eventResultProcessingIsGoing = false;
	Tendiwa.signalAnimationCompleted();
}

boolean isEventProcessingGoing() {
	return eventResultProcessingIsGoing;
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

public TendiwaStage getStage() {
	return stage;
}

public void toggleStatusbar() {
	config.statusbarEnabled = !config.statusbarEnabled;
}

public TextureAtlas getAtlasUi() {
	return atlasUi;
}

public InputProcessor getInputProcessor() {
	return inputMultiplexer;
}

public TendiwaUiStage getUiStage() {
	return uiStage;
}

public ClientConfig getConfig() {
	return config;
}

public Cursor getCursor() {
	return cursor;
}
}
