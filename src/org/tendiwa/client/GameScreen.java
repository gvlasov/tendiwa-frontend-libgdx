package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class GameScreen implements Screen {

static final int TILE_SIZE = 32;
static final ShaderProgram defaultShader = SpriteBatch.createDefaultShader();
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
private final TendiwaGame game;
private final TextureAtlas atlasObjects;
private final GameScreenInputProcessor controller;
private final FloorLayer floorLayer;
private final FloorFieldOfViewLayer floorFieldOfViewLayer;
private final TendiwaStage stage;
private final CellNetLayer cellNetLayer;
private final Cursor cursor;
protected int startCellX;
OrthographicCamera camera;
/**
 * The World object in backend (not always consistent with current animation state, so you shouldn't read from it
 * directly unless absolutely necessary. For listening for changes in the world use {@link org.tendiwa.events.Event}s.
 */
World backendWorld;
RenderPlayer player;
int startCellY;
int centerPixelX;
int centerPixelY;
int cameraMoveStep = 1;
int startPixelX;
int startPixelY;
Map<Integer, RenderCell> cells = new HashMap<>();
private BitmapFont font = new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf")).generateFont(20, "qwertyuiop[]asdfghjkl;'zxcvbnm,./1234567890-=!@#$%^&*()_+QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?\\|", true);
/**
 * Max index of each floor type's images.
 */
private boolean eventResultProcessingIsGoing = false;
/**
 * The greatest value of camera's center pixel on y axis.
 */
private int maxPixelY;
/**
 * The greatest value of camera's center pixel on x axis.
 */
private int maxPixelX;
private Map<Integer, GameObject> objects = new HashMap<>();
private boolean statusbarEnabled = false;

public GameScreen(final TendiwaGame game) {
	this.game = game;
	backendWorld = Tendiwa.getWorld();
	player = new RenderPlayer(backendWorld.getPlayerCharacter());

	worldWidthCells = Tendiwa.getWorld().getWidth();
	worldHeightCells = Tendiwa.getWorld().getHeight();
	windowWidth = game.cfg.width;
	windowHeight = game.cfg.height;
	windowWidthCells = (int) Math.ceil(((float) windowWidth) / TILE_SIZE);
	windowHeightCells = (int) Math.ceil(((float) windowHeight) / TILE_SIZE);

	camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.setToOrtho(true, windowWidth, windowHeight);
	centerCamera(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE);
	camera.update();

	atlasObjects = new TextureAtlas(Gdx.files.internal("pack/objects.atlas"), true);

	TransitionPregenerator.initTileTextureRegionProvider(100);

	// Sprite batch for drawing to world.
	batch = new SpriteBatch();

	maxStartX = worldWidthCells - windowWidthCells - cameraMoveStep;
	maxStartY = worldHeightCells - windowHeightCells - cameraMoveStep;
	maxPixelX = backendWorld.getWidth() * TILE_SIZE - windowWidth / 2;
	maxPixelY = backendWorld.getHeight() * TILE_SIZE - windowHeight / 2;

	depthTestFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, windowWidth, windowHeight, true);

	stage = new TendiwaStage(this);

	controller = new GameScreenInputProcessor(this);
	Gdx.input.setInputProcessor(controller);

	setRenderingMode();

	wallsLayer = new WallsLayer(this);
	floorLayer = new FloorLayer(this);
	floorFieldOfViewLayer = new FloorFieldOfViewLayer(this);
	cellNetLayer = new CellNetLayer(this);
	cursor = new Cursor(this);
}

public static ShaderProgram createShader(FileHandle file) {
	ShaderProgram shader = new ShaderProgram(defaultShader.getVertexShaderSource(), file.readString());
	if (!shader.isCompiled()) {
		Tendiwa.getLogger().error(shader.getLog());
		throw new RuntimeException("Could not compile a shader");
	}
	return shader;
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
	startCellX = (x - x % TILE_SIZE) / TILE_SIZE - windowWidthCells / 2;
	startCellY = (y - y % TILE_SIZE) / TILE_SIZE - windowHeightCells / 2;
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
	cursor.updateCursorCoords();
//	cellNetLayer.draw();
	stage.draw();
	drawObjects();
}

private void drawObjects() {
	batch.begin();
	batch.draw(cursor.getTexture(), cursor.getWorldX() * TILE_SIZE, cursor.getWorldY() * TILE_SIZE);
	for (int x = 0; x < windowWidth / TILE_SIZE + (centerPixelX == maxPixelX ? 0 : 1); x++) {
		// Objects are drawn for one additional row to see high objects
		for (int y = 0; y < windowHeight / TILE_SIZE + (centerPixelY == maxPixelY || centerPixelY == maxPixelY - TILE_SIZE ? 0 : 2); y++) {
			RenderCell cell = cells.get((startCellX + x) * backendWorld.getHeight() + (startCellY + y));
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
				} else {
					// Draw shaded object
				}
			}
		}
	}
	// Draw stats
	RenderCell cellUnderCursor = cells.get(cursor.getWorldX() * backendWorld.getHeight() + cursor.getWorldY());
	if (statusbarEnabled) {
		font.draw(
			batch,
			Gdx.graphics.getFramesPerSecond()
				+ "; " + startCellX + ":" + startCellY + " "
				+ (cellUnderCursor == null ? "" : FloorType.getById(cellUnderCursor.getFloor()).getName())
				+ " cursor: " + cursor.getWorldX() + " " + cursor.getWorldY(),
			startPixelX + 100,
			startPixelY + 100);
	}
	batch.end();
}

boolean hasCell(int x, int y) {
	return cells.containsKey(x * backendWorld.getHeight() + y);
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
	return !(getCell(x, y).hasWall() && !hasCell(x, y + 1));
}

RenderCell getCell(int x, int y) {
	return cells.get(x * Tendiwa.getWorld().getHeight() + y);
}

int getMaxRenderCellX() {
	return startCellX + windowWidthCells + (startPixelX % TILE_SIZE == 0 ? 0 : 1);
}

int getMaxRenderCellY() {
	return startCellY + windowHeightCells + (startPixelY % TILE_SIZE == 0 ? 0 : 1);
}

private void processEvents() {
	Queue<EventResult> queue = game.getEventManager().getPendingOperations();
	if (!eventResultProcessingIsGoing && queue.size() == 0) {
		controller.executeCurrentTask();
	}
	// Loop variable will remain true if it is not set to true inside .process().
	while (!eventResultProcessingIsGoing && !Server.isTurnComputing() && !queue.isEmpty()) {
		EventResult result = queue.remove();
		eventResultProcessingIsGoing = true;
		result.process();
	}
}

void eventProcessingDone() {
	eventResultProcessingIsGoing = false;
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
		gameObject.getType().getName()
	);
}

@Override
public void resize(int width, int height) {

}

@Override
public void show() {
	setRenderingMode();
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
	statusbarEnabled = !statusbarEnabled;
}
}
