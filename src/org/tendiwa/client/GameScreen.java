package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class GameScreen implements Screen {

static final int TILE_SIZE = 32;
static final ShaderProgram defaultShader = SpriteBatch.createDefaultShader();
final Stage stage;
final int maxStartX;
final int maxStartY;
final SpriteBatch batch;
final int windowHeight;
final int windowWidth;
final int worldWidthCells;
final int worldHeightCells;
final FrameBuffer depthTestFrameBuffer;
final ShaderProgram drawOpaqueToDepth05Shader;
final ShaderProgram writeOpaqueToDepthShader;
final ShaderProgram drawWithDepth0Shader;
final TransitionPregenerator fovEdgeOnWallToUnseen;
final TransitionPregenerator fovEdgeOnWallToNotYetSeen;
final ShaderProgram fillWithTransparentBlack;
final ShaderProgram drawWithRGB06Shader;
final ShaderProgram opaque0Transparent05DepthShader;
final WallsLayer wallsLayer;
final int windowWidthCells;
final int windowHeightCells;
private final TendiwaGame game;
private final Texture cursor;
private final TextureAtlas atlasObjects;
private final GameScreenInputProcessor controller;
private final FovEdgeOpaque fovEdgeOpaque;
private final int[] wallHeights;
private final OrthographicCamera oneTileWiderCanera;
private final ShaderProgram drawWithDepth05Shader;
private final FloorLayer floorLayer;
protected int startCellX;
OrthographicCamera camera;
World WORLD;
RenderPlayer PLAYER;
int startCellY;
int centerPixelX;
int centerPixelY;
int cameraMoveStep = 1;
int startPixelX;
int startPixelY;
Map<Integer, RenderCell> cells = new HashMap<>();
private BitmapFont font = new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf")).generateFont(20, "qwertyuiop[]asdfghjkl;'zxcvbnm,./1234567890-=!@#$%^&*()_+QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?\\|", true);
private FrameBuffer cellNetFramebuffer;
private Map<Character, Actor> characterActors = new HashMap<>();
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
private com.badlogic.gdx.graphics.g2d.TextureAtlas atlasWalls;
private Map<Integer, GameObject> objects = new HashMap<>();
private ShapeRenderer shapeRen = new ShapeRenderer();
private int cursorWorldX;
private int cursorWorldY;

public GameScreen(final TendiwaGame game) {
	WORLD = Tendiwa.getWorld();
	PLAYER = new RenderPlayer(WORLD.getPlayerCharacter());

	worldWidthCells = Tendiwa.getWorld().getWidth();
	worldHeightCells = Tendiwa.getWorld().getHeight();
	windowWidth = game.cfg.width;
	windowHeight = game.cfg.height;
	windowWidthCells = (int) Math.ceil(((float) windowWidth) / TILE_SIZE);
	windowHeightCells = (int) Math.ceil(((float) windowHeight) / TILE_SIZE);

	camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.setToOrtho(true, windowWidth, windowHeight);
	centerCamera(PLAYER.getX() * TILE_SIZE, PLAYER.getY() * TILE_SIZE);
	camera.update();

	oneTileWiderCanera = new OrthographicCamera(Gdx.graphics.getWidth() + TILE_SIZE, Gdx.graphics.getHeight() + TILE_SIZE);
	oneTileWiderCanera.setToOrtho(true, windowWidth + TILE_SIZE, windowHeight + TILE_SIZE);

	this.game = game;

	atlasObjects = new TextureAtlas(Gdx.files.internal("pack/objects.atlas"), true);
	atlasWalls = new TextureAtlas(Gdx.files.internal("pack/walls.atlas"), true);

	TransitionPregenerator.initTileTextureRegionProvider(100);

	// Sprite batch for drawing to world.
	batch = new SpriteBatch();
	// Utility batch with no transformations.

	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
	stage.setCamera(camera);

	cursor = buildCursorTexture();

	maxStartX = worldWidthCells - windowWidthCells - cameraMoveStep;
	maxStartY = worldHeightCells - windowHeightCells - cameraMoveStep;

	cellNetFramebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.cfg.width + TILE_SIZE, game.cfg.height + TILE_SIZE, false);
	depthTestFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.cfg.width, game.cfg.height, true);

	buildNet();
	initializeActors();
	controller = new GameScreenInputProcessor(this);
	Gdx.input.setInputProcessor(controller);

	maxPixelX = WORLD.getWidth() * TILE_SIZE - windowWidth / 2;
	maxPixelY = WORLD.getHeight() * TILE_SIZE - windowHeight / 2;

	fovEdgeOpaque = new FovEdgeOpaque();

	setRenderingMode();

	drawOpaqueToDepth05Shader = new ShaderProgram(defaultShader.getVertexShaderSource(), Gdx.files.internal("shaders/drawOpaqueToDepth05.glsl").readString());
	int numberOfWallTypes = WallType.getNumberOfWallTypes();
	wallHeights = new int[numberOfWallTypes];
	for (int i = 0; i < numberOfWallTypes; i++) {
		wallHeights[i] = atlasWalls.findRegion(WallType.getById(i + 1).getName()).getRegionHeight();
	}

	writeOpaqueToDepthShader = createShader(Gdx.files.internal("shaders/writeOpaqueToDepth.f.glsl"));
	drawWithDepth0Shader = createShader(Gdx.files.internal("shaders/drawWithDepth0.f.glsl"));
	fovEdgeOnWallToUnseen = new FovEdgeTransparent();
	fovEdgeOnWallToNotYetSeen = new FovEdgeOpaque();
	fillWithTransparentBlack = createShader(Gdx.files.internal("shaders/fillWithTransparentBlack.f.glsl"));
	drawWithRGB06Shader = createShader(Gdx.files.internal("shaders/drawWithRGB06.f.glsl"));
	drawWithDepth05Shader = createShader(Gdx.files.internal("shaders/drawWithDepth05.f.glsl"));
	opaque0Transparent05DepthShader = createShader(Gdx.files.internal("shaders/opaque0transparent05depth.f.glsl"));

	wallsLayer = new WallsLayer(this);
	floorLayer = new FloorLayer(this);
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
 * Sets continuous rendering. Needed for restoration of this Screen after switching from another screen with
 * non-continuous rendering.
 */
private void setRenderingMode() {
	Gdx.graphics.setContinuousRendering(true);
	Gdx.graphics.requestRendering();
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

@Override
public void render(float delta) {
	Actor characterActor = getCharacterActor(Tendiwa.getPlayer());
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
	applyUnseenBrightnessMap();
	wallsLayer.draw();
	updateCursorCoords();
	drawNet();
	stage.draw();
	drawObjects();
}

private void updateCursorCoords() {
	int cursorX = Gdx.input.getX();
	int cursorY = Gdx.input.getY();
	int cursorScreenCoordX = (cursorX - cursorX % TILE_SIZE);
	int cursorScreenCoordY = (cursorY - cursorY % TILE_SIZE);
	cursorWorldX = startCellX + cursorScreenCoordX / TILE_SIZE;
	cursorWorldY = startCellY + cursorScreenCoordY / TILE_SIZE;
}

private void drawObjects() {
	batch.begin();
	// But first drawWorld cursor before drawing objects
	batch.draw(cursor, cursorWorldX * TILE_SIZE, cursorWorldY * TILE_SIZE);
	for (int x = 0; x < windowWidth / TILE_SIZE + (centerPixelX == maxPixelX ? 0 : 1); x++) {
		// Objects are drawn for one additional row to see high objects
		for (int y = 0; y < windowHeight / TILE_SIZE + (centerPixelY == maxPixelY || centerPixelY == maxPixelY - TILE_SIZE ? 0 : 2); y++) {
			RenderCell cell = cells.get((startCellX + x) * WORLD.getHeight() + (startCellY + y));
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
	RenderCell cellUnderCursor = cells.get(cursorWorldX * WORLD.getHeight() + cursorWorldY);
	font.draw(
		batch,
		Gdx.graphics.getFramesPerSecond()
			+ "; " + startCellX + ":" + startCellY + " "
			+ (cellUnderCursor == null ? "" : FloorType.getById(cellUnderCursor.getFloor()).getName())
			+ " cursor: " + cursorWorldX + " " + cursorWorldY,
		startPixelX + 100,
		startPixelY + 100);
	batch.end();
}

int getWallHeight(short terrain) {
	return wallHeights[terrain];
}

private void applyUnseenBrightnessMap() {
	depthTestFrameBuffer.begin();

	fovEdgeOpaque.batch.setProjectionMatrix(camera.combined);
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_LESS);
	Gdx.gl.glDepthMask(true);
	Gdx.gl.glColorMask(false, false, false, false);

	shapeRen.setProjectionMatrix(camera.combined);
	shapeRen.begin(ShapeRenderer.ShapeType.Filled);
	shapeRen.setColor(1, 0, 0, 0.5f);
	int maxRenderCellX = getMaxRenderCellX();
	int maxRenderCellY = getMaxRenderCellY();
	for (int x = startCellX; x < maxRenderCellX; x++) {
		for (int y = startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null) {
				if (!cell.isVisible()) {
					shapeRen.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
			}
		}
	}
	shapeRen.end();
	Gdx.gl.glColorMask(true, true, true, true);

	// Draw transitions to unseen cells (half-transparent)
	fovEdgeOpaque.batch.setShader(fovEdgeOpaque.halfTransparencyShader);
	fovEdgeOpaque.batch.begin();
	for (int x = startCellX; x < maxRenderCellX; x++) {
		for (int y = startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null && cell.isVisible()) {
				boolean[] hasUnseenNeighbors = getHasUnseenNeighbors(x, y);
				if (hasUnseenNeighbors[0] || hasUnseenNeighbors[1] || hasUnseenNeighbors[2] || hasUnseenNeighbors[3]) {
					fovEdgeOpaque.drawTransitions(
						fovEdgeOpaque.batch,
						x * TILE_SIZE,
						y * TILE_SIZE,
						hasUnseenNeighbors,
						x + windowWidthCells - PLAYER.getX(),
						y - windowHeightCells - PLAYER.getY()
					);
				}
			}
		}
	}
	fovEdgeOpaque.batch.end();

	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_EQUAL);

	shapeRen.begin(ShapeRenderer.ShapeType.Filled);
	shapeRen.setColor(0, 0, 0, 0.4f);
	// Draw black transparent color above mask
	shapeRen.rect(startCellX * TILE_SIZE, startCellY * TILE_SIZE, windowWidth + TILE_SIZE, windowHeight + TILE_SIZE);
	shapeRen.end();

	// Draw transitions to not yet seen cells
	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	fovEdgeOpaque.batch.setShader(defaultShader);
	fovEdgeOpaque.batch.begin();
	for (int x = startCellX; x < maxRenderCellX; x++) {
		for (int y = startCellY; y < maxRenderCellY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null) {
				int hashX, hashY;
				if (cell.isVisible()) {
					hashX = x + windowWidthCells - PLAYER.getX();
					hashY = y + windowHeightCells - PLAYER.getY();
				} else {
					hashX = x;
					hashY = y;
				}
				fovEdgeOpaque.drawTransitions(
					fovEdgeOpaque.batch,
					x * TILE_SIZE,
					y * TILE_SIZE,
					getHasNotYetSeenNeighbors(x, y),
					hashX,
					hashY
				);
			}
		}
	}
	fovEdgeOpaque.batch.end();

	depthTestFrameBuffer.end();

	batch.begin();
	batch.draw(depthTestFrameBuffer.getColorBufferTexture(), startPixelX, startPixelY);
	batch.end();

}

private boolean[] getHasNotYetSeenNeighbors(int x, int y) {
	return new boolean[]{
		!hasCell(x, y - 1),
		!hasCell(x + 1, y),
		!hasCell(x, y + 1),
		!hasCell(x - 1, y)
	};
}

private boolean[] getHasUnseenNeighbors(int x, int y) {
	return new boolean[]{
		hasCell(x, y - 1) && !getCell(x, y - 1).isVisible(),
		hasCell(x + 1, y) && !getCell(x + 1, y).isVisible(),
		hasCell(x, y + 1) && !getCell(x, y + 1).isVisible(),
		hasCell(x - 1, y) && !getCell(x - 1, y).isVisible()
	};
}

boolean hasCell(int x, int y) {
	return cells.containsKey(x * WORLD.getHeight() + y);
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

TextureRegion getWallTextureByCell(int x, int y) {
	short wallId = cells.get(x * WORLD.getHeight() + y).getWall();
	WallType wallType = WallType.getById(wallId);
	String name = wallType.getName();
	int index = 0;
	RenderCell neighborCell = cells.get(x * WORLD.getHeight() + (y - 1));
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 1000;
	}
	neighborCell = cells.get((x + 1) * WORLD.getHeight() + y);
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 100;
	}
	neighborCell = cells.get(x * WORLD.getHeight() + (y + 1));
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 10;
	}
	neighborCell = cells.get((x - 1) * WORLD.getHeight() + y);
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 1;
	}
	return atlasWalls.findRegion(name, index);
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

private Actor createCharacterActor(Character character) {
	return new CharacterActor(character);
}

private void initializeActors() {
	TimeStream timeStream = WORLD.getPlayerCharacter().getTimeStream();
	for (Character character : timeStream.getCharacters()) {
		Actor actor = createCharacterActor(character);
		characterActors.put(character, actor);
		stage.addActor(actor);
	}
}

private void buildNet() {
	cellNetFramebuffer.begin();

	// Imitating the same camera as for batch, but with a greater viewport.
	OrthographicCamera adHocCamera = new OrthographicCamera(windowWidth + TILE_SIZE, windowHeight + TILE_SIZE);
	adHocCamera.setToOrtho(true, windowWidth + TILE_SIZE, windowHeight + TILE_SIZE);

	ShapeRenderer shapeRenderer = new ShapeRenderer();
	shapeRenderer.setProjectionMatrix(adHocCamera.combined);
	shapeRenderer.setColor(0, 0, 0, 0.1f);
	shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	for (int cellX = 0; cellX < windowWidth / TILE_SIZE + 2; cellX++) {
		shapeRenderer.line(cellX * TILE_SIZE, 0, cellX * TILE_SIZE, windowHeight + TILE_SIZE);
	}
	for (int cellY = 0; cellY < windowWidth / TILE_SIZE + 2; cellY++) {
		shapeRenderer.line(0, cellY * TILE_SIZE, windowWidth + TILE_SIZE, cellY * TILE_SIZE);
	}
	shapeRenderer.end();
	cellNetFramebuffer.end();
}

private void drawNet() {
	batch.begin();
	batch.draw(cellNetFramebuffer.getColorBufferTexture(), startCellX * TILE_SIZE, startCellY * TILE_SIZE);
	batch.end();
}

private TextureAtlas.AtlasRegion getObjectTextureByCell(int x, int y) {
	GameObject gameObject = objects.get(x * WORLD.getHeight() + y);
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

Texture buildCursorTexture() {
	Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(1, 1, 0, 0.3f);
	pixmap.fillRectangle(0, 0, 31, 31);
	return new Texture(pixmap);
}

public Actor getCharacterActor(Character character) {
	return characterActors.get(character);
}

}
