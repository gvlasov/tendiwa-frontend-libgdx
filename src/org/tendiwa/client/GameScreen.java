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
import com.badlogic.gdx.utils.Array;
import tendiwa.core.*;
import tendiwa.core.Character;
import tendiwa.core.meta.Chance;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class GameScreen implements Screen {

static final int TILE_SIZE = 32;
private static final ShaderProgram defaultShader = SpriteBatch.createDefaultShader();
final Stage stage;
final int maxStartX;
final int maxStartY;
private final TendiwaGame game;
private final PixmapTextureAtlas pixmapTextureAtlasFloors;
private final SpriteBatch batch;
private final int windowHeight;
private final int windowWidth;
private final Map<CardinalDirection, Map<Integer, Pixmap>> floorTransitions = new HashMap<>();
private final Texture cursor;
private final int windowWidthCells;
private final int windowHeightCells;
private final TextureAtlas atlasFloors;
private final TextureAtlas atlasObjects;
private final int transitionsAtlasSize = 1024;
private final FrameBuffer transitionsFrameBuffer;
private final SpriteBatch defaultBatch;
private final GameScreenInputProcessor controller;
private final int worldWidthCells;
private final int worldHeightCells;
private final FrameBuffer depthTestFrameBuffer;
private final TransitionPregenerator fovEdgeOpaque;
private final ShaderProgram drawOpaqueToDepth05Shader;
private final int[] wallHeights;
private final ShaderProgram writeOpaqueToDepthShader;
private final ShaderProgram drawWithDepth0Shader;
private final TransitionPregenerator fovEdgeOnWallToUnseen;
private final TransitionPregenerator fovEdgeOnWallToNotYetSeen;
private final ShaderProgram fillWithTransparentBlack;
private final ShaderProgram drawWithRGB06Shader;
private final OrthographicCamera oneTileWiderCanera;
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
private Texture bufTexture0 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture1 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture2 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture3 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Map<String, TextureRegion> transitionsMap = new HashMap<>();
private FrameBuffer cellNetFramebuffer;
private Map<Character, Actor> characterActors = new HashMap<>();
private boolean eventResultProcessingIsGoing = false;
private Map<Integer, TextureRegion> floorRegions = new HashMap<>();
/**
 * Max index of each floor type's images.
 */
private Map<Short, Integer> floorIndices = new HashMap<>();
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
	windowWidthCells = (int) Math.ceil(((float) windowWidth) / 32);
	windowHeightCells = (int) Math.ceil(((float) windowHeight) / 32);

	camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.setToOrtho(true, windowWidth, windowHeight);
	centerCamera(PLAYER.getX() * TILE_SIZE, PLAYER.getY() * TILE_SIZE);
	camera.update();

	oneTileWiderCanera = new OrthographicCamera(Gdx.graphics.getWidth()+TILE_SIZE, Gdx.graphics.getHeight()+TILE_SIZE);
	oneTileWiderCanera.setToOrtho(true, windowWidth+TILE_SIZE, windowHeight+TILE_SIZE);

	this.game = game;

	atlasFloors = new TextureAtlas(Gdx.files.internal("pack/floors.atlas"), true);
	atlasObjects = new TextureAtlas(Gdx.files.internal("pack/objects.atlas"), true);
	atlasWalls = new TextureAtlas(Gdx.files.internal("pack/walls.atlas"), true);
	pixmapTextureAtlasFloors = createPixmapTextureAtlas("floors");

	// Sprite batch for drawing to world.
	batch = new SpriteBatch();
	// Utility batch with no transformations.
	defaultBatch = new SpriteBatch();

	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
	stage.setCamera(camera);

	cursor = buildCursorTexture();

	maxStartX = worldWidthCells - windowWidthCells - cameraMoveStep;
	maxStartY = worldHeightCells - windowHeightCells - cameraMoveStep;

	transitionsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.cfg.width, game.cfg.height, false);
	cellNetFramebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.cfg.width + TILE_SIZE, game.cfg.height + TILE_SIZE, false);
	depthTestFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.cfg.width, game.cfg.height, true);

	buildNet();
	initializeActors();
	controller = new GameScreenInputProcessor(this);
	Gdx.input.setInputProcessor(controller);

	cacheRegions();

	maxPixelX = WORLD.getHeight() * TILE_SIZE - windowWidth / 2;
	maxPixelY = WORLD.getHeight() * TILE_SIZE - windowHeight / 2;

	fovEdgeOpaque = new FovEdgeOpaque();

//	Gdx.graphics.setContinuousRendering(false);
//	Gdx.graphics.requestRendering();

	drawOpaqueToDepth05Shader = new ShaderProgram(defaultShader.getVertexShaderSource(), Gdx.files.internal("shaders/drawOpaqueToDepth05.glsl").readString());
	int numberOfWallTypes = TerrainType.getNumberOfWallTypes();
	wallHeights = new int[numberOfWallTypes];
	for (int i = 0; i < numberOfWallTypes; i++) {
		wallHeights[i] = atlasWalls.findRegion(TerrainType.getById(-i - 1).getName()).getRegionHeight();
	}

	writeOpaqueToDepthShader = createShader(Gdx.files.internal("shaders/writeOpaqueToDepth.f.glsl"));
	drawWithDepth0Shader = createShader(Gdx.files.internal("shaders/drawWithDepth0.f.glsl"));
	fovEdgeOnWallToUnseen = new FovEdgeTransparent();
	fovEdgeOnWallToNotYetSeen = new FovEdgeOpaque();
	fillWithTransparentBlack = createShader(Gdx.files.internal("shaders/fillWithTransparentBlack.f.glsl"));
	drawWithRGB06Shader = createShader(Gdx.files.internal("shaders/drawWithRGB06.f.glsl"));
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

private PixmapTextureAtlas createPixmapTextureAtlas(String name) {
	return new PixmapTextureAtlas(Gdx.files.internal("pack/" + name + ".png"), Gdx.files.internal("pack/" + name + ".atlas"));
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

	drawFloor();
	drawTransitions();
	applyUnseenBrightnessMap();
	drawWalls();
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
			+ (cellUnderCursor == null ? "" : TerrainType.getById(cellUnderCursor.getTerrain()).getName())
			+ " cursor: " + cursorWorldX + " " + cursorWorldY,
		startPixelX + 100,
		startPixelY + 100);
	batch.end();
}

private void drawTransitions() {
	// Draw transitions
	for (int x = 0; x < windowWidth / TILE_SIZE; x++) {
		for (int y = 0; y < windowHeight / TILE_SIZE; y++) {
			RenderCell cell = cells.get((startCellX + x) * WORLD.getHeight() + (startCellY + y));
			if (cell != null) {
				if (TerrainType.getById(cell.getTerrain()).getTerrainClass() == TerrainType.TerrainClass.FLOOR) {
					drawFloorTransitionsInCell(cell);
				}
			}
		}
	}
}

private void drawWalls() {
	// There is a complexity in drawing walls: drawing transitions above walls.
	// These transitions go on the "roof" of a wall, i.e. higher than floor transitions.
	depthTestFrameBuffer.begin();
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_ALWAYS);

	int maxX = getMaxRenderCellX();
	int maxY = getMaxRenderCellY() + 1; // +1 here is to draw walls that start below viewport

	// Draw visible walls.
	// For each opaque fragment of a visible wall, we place a 0 to the depth buffer.
	// This will later indicate a mask for drawing transitions on walls.
	batch.setShader(writeOpaqueToDepthShader);
	batch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	// SpriteBatch disables depth buffer with glDepthMask(false) internally,
	// so we have to reenable it to properly wirte our depth mask.
	Gdx.gl.glDepthMask(true);
	for (int x = startCellX; x < maxX; x++) {
		for (int y = startCellY; y < maxY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null && cell.isVisible()) {
				if (TerrainType.getById(cell.getTerrain()).getTerrainClass() == TerrainType.TerrainClass.WALL) {
					TextureRegion wall = getWallTextureByCell(x, y);
					int wallTextureHeight = wall.getRegionHeight();
					batch.draw(wall, x * TILE_SIZE, y * TILE_SIZE - (wallTextureHeight - TILE_SIZE));
					RenderCell cellFromSouth = getCell(x, y + 1);
					if (cellFromSouth != null && !cellFromSouth.isVisible()) {
						if (TerrainType.getById(cell.getTerrain()).getTerrainClass() == TerrainType.TerrainClass.WALL &&
							TerrainType.getById(cellFromSouth.getTerrain()).getTerrainClass() != TerrainType.TerrainClass.WALL
							) {
							// Draw shaded south front faces of unseen walls that don't have a wall neighbor from south.
							// For that we'll need to update the depth mask from scratch, so we clear depth buffer to 1.0.
							// It will consist only of rectangles covering those wall sides.
							batch.setShader(drawOpaqueToDepth05Shader);
							int wallSideHeight = wallTextureHeight - TILE_SIZE;
							int origY = wall.getRegionY();
							int origX = wall.getRegionX();
							wall.setRegion(origX, origY, TILE_SIZE, -wallSideHeight);
							Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
							batch.draw(
								wall,
								x * TILE_SIZE,
								y * TILE_SIZE + TILE_SIZE - wallTextureHeight + TILE_SIZE,
								TILE_SIZE,
								wallSideHeight
							);
							Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
							wall.setRegion(origX, origY, TILE_SIZE, -wallTextureHeight);
							batch.setShader(writeOpaqueToDepthShader);

						}
					}
				}
			}
		}
	}
	batch.end();

	// Draw unseen walls
	batch.setShader(drawWithDepth0Shader);
	batch.begin();
	Gdx.gl.glDepthMask(true);
	for (int x = startCellX; x < maxX; x++) {
		for (int y = startCellY; y < maxY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null && !cell.isVisible()) {
				if (TerrainType.getById(cell.getTerrain()).getTerrainClass() == TerrainType.TerrainClass.WALL) {
					TextureRegion wall = getWallTextureByCell(x, y);
					batch.draw(wall, x * TILE_SIZE, y * TILE_SIZE - (wall.getRegionHeight() - TILE_SIZE));
				}
			}
		}
	}
	batch.end();

	// Create mask for transitions upon seen walls
	Gdx.gl.glDepthFunc(GL10.GL_GREATER);
	batch.setShader(drawOpaqueToDepth05Shader);
	batch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glColorMask(false, false, false, false);
	Gdx.gl.glDepthMask(true);
	for (int x = startCellX; x < maxX; x++) {
		for (int y = startCellY; y < maxY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null) {
				if (TerrainType.getById(cell.getTerrain()).getTerrainClass() == TerrainType.TerrainClass.WALL) {
					drawTransitionsOnWall(x, y, cell);
				}
			}
		}
	}
	batch.end();

	// Draw seen walls again above the mask
	Gdx.gl.glColorMask(true, true, true, true);
	Gdx.gl.glDepthFunc(GL10.GL_EQUAL);
	batch.setShader(drawWithRGB06Shader);
	batch.begin();
	for (int x = startCellX; x < maxX; x++) {
		for (int y = startCellY; y < maxY; y++) {
			RenderCell cell = getCell(x, y);
			if (cell != null && cell.isVisible()) {
				if (TerrainType.getById(cell.getTerrain()).getTerrainClass() == TerrainType.TerrainClass.WALL) {
					TextureRegion wall = getWallTextureByCell(x, y);
					int wallTextureHeight = wall.getRegionHeight();
					batch.draw(wall, x * TILE_SIZE, y * TILE_SIZE - (wallTextureHeight - TILE_SIZE));
				}
			}
		}
	}
	batch.end();

	Gdx.gl.glDepthMask(false);
	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);

	batch.setShader(defaultShader);

	depthTestFrameBuffer.end();

	batch.begin();
	batch.draw(depthTestFrameBuffer.getColorBufferTexture(), startPixelX, startPixelY);
	batch.end();

}

private void drawTransitionsOnWall(int x, int y, RenderCell cell) {
	int wallHeight = getWallHeight(cell.getTerrain());
	for (CardinalDirection dir : CardinalDirection.values()) {
		// Here to get texture number shift we pass absolute coordinates x and y, because,
		// unlike in applyUnseenBrightnessMap(),  here position of transition in not relative to viewport.
		int[] d = dir.side2d();
		RenderCell neighborCell = getCell(x + d[0], y + d[1]);
		TextureRegion transition = null;
		boolean noNeighbor = neighborCell == null;
		if (noNeighbor) {
			Gdx.gl.glColorMask(true, true, true, true);
			Gdx.gl.glDepthMask(false);
			transition = fovEdgeOnWallToNotYetSeen.getTransition(dir, x, y);
		} else if (cell.isVisible() && !neighborCell.isVisible()) {
			Gdx.gl.glColorMask(false, false, false, false);
			Gdx.gl.glDepthMask(true);
			transition = fovEdgeOnWallToUnseen.getTransition(dir, x, y);
		}
		if (transition != null) {
			batch.draw(transition, x * TILE_SIZE, y * TILE_SIZE - wallHeight + TILE_SIZE);
//			if (noNeighbor) {
				batch.end();
				Gdx.gl.glColorMask(false, false, false, false);
				Gdx.gl.glDepthMask(true);
				batch.begin();
//			}
		}
	}
}

private int getWallHeight(short terrain) {
	return wallHeights[-terrain];
}

private void drawFloor() {
	int maxX = getMaxRenderCellX();
	int maxY = getMaxRenderCellY();
	batch.begin();
	for (int x = startCellX; x < maxX; x++) {
		for (int y = startCellY; y < maxY; y++) {
			RenderCell cell = cells.get(x * WORLD.getHeight() + y);
			if (cell != null) {
				short terrain = cell.getTerrain();
				if (TerrainType.getById(terrain).getTerrainClass() == TerrainType.TerrainClass.FLOOR) {
					drawFloor(terrain, x, y);
				} else {
					assert TerrainType.getById(terrain).getTerrainClass() == TerrainType.TerrainClass.WALL;
					drawFloorUnderWall(x, y);
				}
			}

		}
	}
	batch.end();
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
	shapeRen.rect(startCellX * TILE_SIZE, startCellY * TILE_SIZE, windowWidth+TILE_SIZE, windowHeight+TILE_SIZE);
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

private boolean hasCell(int x, int y) {
	return cells.containsKey(x * WORLD.getHeight() + y);
}

private void drawFloorUnderWall(int x, int y) {
	RenderCell cellOneRowLower = getCell(x, y + 1);
	if (cellOneRowLower == null) {
		return;
	}
	int[] dx = new int[]{0, 1, 0, -1};
	int[] dy = new int[]{-1, 0, 1, 0};
	for (int i = 0; i < 4; i++) {
		RenderCell cell = getCell(x + dx[i], y + dy[i]);
		if (cell == null) {
			continue;
		}
		short terrain = cell.getTerrain();
		if (TerrainType.isFloor(terrain)) {
			drawFloor(terrain, x, y);
			break;
		}
	}
}

private void drawFloor(short terrain, int x, int y) {
	TextureRegion floor = getFloorTextureByCell(terrain, x, y);
	batch.draw(floor, x * TILE_SIZE, y * TILE_SIZE);
}

RenderCell getCell(int x, int y) {
	return cells.get(x * Tendiwa.getWorld().getHeight() + y);
}

private TextureRegion getWallTextureByCell(int x, int y) {
	short terrain = cells.get(x * WORLD.getHeight() + y).getTerrain();
	TerrainType wallType = TerrainType.getById(terrain);
	String name = wallType.getName();
	assert wallType.getTerrainClass() == TerrainType.TerrainClass.WALL;
	int index = 0;
	RenderCell neighborCell = cells.get(x * WORLD.getHeight() + (y - 1));
	if (neighborCell == null || neighborCell.getTerrain() == terrain) {
		index += 1000;
	}
	neighborCell = cells.get((x + 1) * WORLD.getHeight() + y);
	if (neighborCell == null || neighborCell.getTerrain() == terrain) {
		index += 100;
	}
	neighborCell = cells.get(x * WORLD.getHeight() + (y + 1));
	if (neighborCell == null || neighborCell.getTerrain() == terrain) {
		index += 10;
	}
	neighborCell = cells.get((x - 1) * WORLD.getHeight() + y);
	if (neighborCell == null || neighborCell.getTerrain() == terrain) {
		index += 1;
	}
	return atlasWalls.findRegion(name, index);
}

private int getMaxRenderCellX() {
	return startCellX + windowWidthCells + (startPixelX % TILE_SIZE == 0 ? 0 : 1);
}

private int getMaxRenderCellY() {
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

private void drawFloorTransitionsInCell(RenderCell cell) {
	int self = cells.get(cell.getX() * WORLD.getHeight() + cell.getY()).getTerrain();
	RenderCell renderCell = cells.get(cell.getX() * WORLD.getHeight() + (cell.getY() + 1));
	int north = cell.getY() + 1 < worldHeightCells && renderCell != null ? renderCell.getTerrain() : self;
	if (!TerrainType.isFloor(north)) {
		north = self;
	}
	renderCell = cells.get((cell.getX() + 1) * WORLD.getHeight() + cell.getY());
	int east = cell.getX() + 1 < worldWidthCells && renderCell != null ? renderCell.getTerrain() : self;
	if (!TerrainType.isFloor(east)) {
		east = self;
	}

	renderCell = cells.get(cell.getX() * WORLD.getHeight() + (cell.getY() - 1));
	int south = cell.getY() > 0 && renderCell != null ? renderCell.getTerrain() : self;
	if (!TerrainType.isFloor(south)) {
		south = self;
	}
	renderCell = cells.get((cell.getX() - 1) * WORLD.getHeight() + cell.getY());
	int west = cell.getX() > 0 && renderCell != null ? renderCell.getTerrain() : self;
	if (!TerrainType.isFloor(west)) {
		west = self;
	}

	if (north != self || east != self || south != self || west != self) {
		drawCellWithTransitions(cell.getX(), cell.getY(), north, east, south, west);
	}
}

private void cacheRegions() {
	Map<String, Short> floorTypeName2id = new HashMap<>();
	Map<String, Array<TextureAtlas.AtlasRegion>> name2regions = new HashMap<>();
	for (Map.Entry<Short, TerrainType> e : TerrainType.getAll().entrySet()) {
		if (e.getValue().getTerrainClass() != TerrainType.TerrainClass.FLOOR) {
			continue;
		}
		floorTypeName2id.put(e.getValue().getName(), e.getKey());
	}
	for (TextureAtlas.AtlasRegion region : atlasFloors.getRegions()) {
		if (floorTypeName2id.containsKey(region.name)) {
			floorRegions.put(floorTypeName2id.get(region.name) * (1 << 15) + region.index, region);
		} else {
			Tendiwa.getLogger().warn("Floor with name " + region.name + "_" + region.index + " has its sprite, but it is not declared in ontology");
		}
	}
	for (Map.Entry<String, Short> e : floorTypeName2id.entrySet()) {
		Array<TextureAtlas.AtlasRegion> regions = atlasFloors.findRegions(e.getKey());
		name2regions.put(e.getKey(), regions);
		floorIndices.put(e.getValue(), regions.size - 1);
	}
	// Validate that all images have indices from 0 to n without skips.
	for (Map.Entry<String, Array<TextureAtlas.AtlasRegion>> e : name2regions.entrySet()) {
		boolean zeroIndexFound = false;
		boolean lastIndexFound = false;
		int size = e.getValue().size;
		for (TextureAtlas.AtlasRegion region : e.getValue()) {
			if (region.index == 0) {
				zeroIndexFound = true;
			}
			if (region.index == size - 1) {
				lastIndexFound = true;
			}
		}
		if (!zeroIndexFound || !lastIndexFound) {
			throw new RuntimeException("Floor images with name \"" + e.getKey() + "\" have wrong indices. Indices of images must start with 0 and don't skip any integer values.");
		}
	}

}

private TextureRegion getFloorTextureByCell(short terrain, int x, int y) {
	int floorId = (int) terrain;
	int key = (int) ((floorId * (1 << 15)) + Math.round(Math.abs(Math.sin(x * y)) * floorIndices.get((short) floorId)));
	return floorRegions.get(key);
}

private void drawCellWithTransitions(int x, int y, int north, int east, int south, int west) {
	// Get individual transition pixmap for each side
	TextureRegion region = getTransition(north, east, south, west);
	batch.begin();
	batch.draw(region, x * TILE_SIZE, y * TILE_SIZE);
	batch.end();
}

private TextureRegion getTransition(int north, int east, int south, int west) {
	String transitionKey = north + "_" + east + "_" + south + "_" + west;
	if (transitionsMap.containsKey(transitionKey)) {
		return transitionsMap.get(transitionKey);
	} else {
		return createNewFloorTransitionRegion(north, east, south, west);
	}
}

private TextureRegion createNewFloorTransitionRegion(int north, int east, int south, int west) {
	String transitionKey = north + "_" + east + "_" + south + "_" + west;

	Pixmap.Blending previousBlending = Pixmap.getBlending();
	Pixmap.setBlending(Pixmap.Blending.None);
	Pixmap transE = getTransition(Directions.E, east);
	Pixmap transW = getTransition(Directions.W, west);
	Pixmap transN = getTransition(Directions.N, north);
	Pixmap transS = getTransition(Directions.S, south);
	int atlasX = transitionsMap.size() * TILE_SIZE % transitionsAtlasSize;
	int atlasY = transitionsMap.size() * TILE_SIZE / transitionsAtlasSize * TILE_SIZE;

	Pixmap.setBlending(Pixmap.Blending.SourceOver);

	bufTexture0.draw(transN, 0, 0);
	bufTexture1.draw(transE, 0, 0);
	bufTexture2.draw(transS, 0, 0);
	bufTexture3.draw(transW, 0, 0);
	transitionsFrameBuffer.begin();
	defaultBatch.begin();
	defaultBatch.draw(bufTexture0, atlasX, atlasY);
	defaultBatch.draw(bufTexture1, atlasX, atlasY);
	defaultBatch.draw(bufTexture2, atlasX, atlasY);
	defaultBatch.draw(bufTexture3, atlasX, atlasY);
	defaultBatch.end();
	transitionsFrameBuffer.end();

	TextureRegion textureRegion = new TextureRegion(transitionsFrameBuffer.getColorBufferTexture(), atlasX, atlasY, TILE_SIZE, TILE_SIZE);
	transitionsMap.put(transitionKey, textureRegion);

	Pixmap.setBlending(previousBlending);
	return textureRegion;
}

private Pixmap getTransition(CardinalDirection dir, int floorId) {
	if (!floorTransitions.containsKey(dir)) {
		floorTransitions.put(dir, new HashMap<Integer, Pixmap>());
	}
	if (!floorTransitions.get(dir).containsKey(floorId)) {
		floorTransitions.get(dir).put(floorId, createTransition(dir, floorId));
	}
	return floorTransitions.get(dir).get(floorId);
}

private Pixmap createTransition(CardinalDirection dir, int floorId) {
	int diffusionDepth = 13;
	if (dir.isVertical()) {
		dir = dir.opposite();
	}
	Pixmap.setBlending(Pixmap.Blending.None);
	Pixmap pixmap = pixmapTextureAtlasFloors.createPixmap(TerrainType.getById(floorId).getName());
	CardinalDirection opposite = dir.opposite();
	EnhancedRectangle transitionRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(dir).createRectangle(diffusionDepth);
	EnhancedRectangle clearRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(opposite).createRectangle(TILE_SIZE - diffusionDepth);
	pixmap.setColor(0, 0, 0, 0);
	// Fill the most of the pixmap with transparent pixels.
	pixmap.fillRectangle(clearRec.x, clearRec.y, clearRec.width, clearRec.height);
	Segment sideSegment = transitionRec.getSideAsSegment(dir);
	EnhancedPoint point = new EnhancedPoint(sideSegment.x, sideSegment.y);
	pixmap.setColor(0, 0, 0, 0);
	CardinalDirection dynamicGrowingDir = dir.isVertical() ? Directions.E : Directions.S;
	int startI = sideSegment.getStaticCoord();
	int oppositeGrowing = opposite.getGrowing();
	int iterationsI = 0;
	for (
		int i = startI;
		i != startI + (diffusionDepth + 1) * opposite.getGrowing();
		i += oppositeGrowing
		) {
		for (
			int j = sideSegment.getStartCoord();
			j <= sideSegment.getEndCoord();
			j += 1
			) {
			if (Chance.roll((i - startI) / oppositeGrowing * 100 / diffusionDepth + 50)) {
				// Set transparent pixels to leave only some non-transparent ones.
				pixmap.drawPixel(point.x, point.y);
			}
			point.moveToSide(dynamicGrowingDir);
		}
		point.setLocation(sideSegment.x, sideSegment.y);
		point.moveToSide(opposite, iterationsI++);
	}
	return pixmap;
}

@Override
public void resize(int width, int height) {

}

@Override
public void show() {

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
	Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
	pixmap.setColor(1, 1, 0, 0.3f);
	pixmap.fillRectangle(0, 0, 31, 31);
	return new Texture(pixmap);
}

public Actor getCharacterActor(Character character) {
	return characterActors.get(character);
}

}
