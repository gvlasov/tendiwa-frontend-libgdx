package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import org.tendiwa.entities.WallTypes;
import tendiwa.core.CardinalDirection;
import tendiwa.core.RenderCell;
import tendiwa.core.RenderWorld;
import tendiwa.core.WallType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class renders walls and field of view transitions above walls. I separated it from other render code because
 * there is a lot of spaghetti code concerning walls and I don't know how to get rid of it.
 */
public class WallActor extends Actor {
public static final Map<WallType, WallImageCache> caches = new HashMap<>();
static final ShaderProgram writeOpaqueToDepthShader = GameScreen.createShader(Gdx.files.internal("shaders/writeOpaqueToDepth.f.glsl"));
static final ShaderProgram drawOpaqueToDepth05Shader = new ShaderProgram(GameScreen.defaultShader.getVertexShaderSource(), Gdx.files.internal("shaders/drawOpaqueToDepth05.glsl").readString());
static final ShaderProgram drawWithDepth0Shader = GameScreen.createShader(Gdx.files.internal("shaders/drawWithDepth0.f.glsl"));
static final ShaderProgram opaque0Transparent05DepthShader = GameScreen.createShader(Gdx.files.internal("shaders/opaque0transparent05depth.f.glsl"));
static final TransitionPregenerator fovEdgeOnWallToUnseen = new FovEdgeTransparent();
static final TransitionPregenerator fovEdgeOnWallToNotYetSeen = new FovEdgeOpaque();
private static final Map<WallType, Integer> wallHeights;
private static final FrameBuffer depthTestFrameBuffer;
private static final Batch depthTestBatch;
private static final int NUMBER_OF_SLOTS_IN_CACHE = 144;
static int maxHeight = 0;

static {
	Array<TextureAtlas.AtlasRegion> regions = AtlasWalls.getInstance().getRegions();
	int numberOfWallTypes = regions.size;
	wallHeights = new HashMap<>(numberOfWallTypes);
	int maxWidth = 0;
	for (TextureAtlas.AtlasRegion region : regions) {
		// Save height of a particular wall
		wallHeights.put(
			WallTypes.getWallTypeByResourceName(region.name),
			region.getRegionHeight()
		);
		// And in the same iteration determine what size of framebuffer is needed.
		if (maxWidth < region.getRegionWidth()) {
			maxWidth = region.getRegionWidth();
		}
		if (maxHeight < region.getRegionHeight()) {
			maxHeight = region.getRegionHeight();
		}
	}
	// Determine what is the biggest size of a wall sprite,
	// and create a framebuffer broad and tall enough to hold any wall sprite.
	depthTestFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, maxWidth, maxHeight, true);
	depthTestBatch = new OrthoBatch(maxWidth, maxHeight);
}

final GameScreen gameScreen;
private final RenderWorld renderWorld;
private final int y;
private final int x;
private final WallType type;

WallActor(GameScreen gameScreen, int x, int y, WallType type) {
	this.gameScreen = gameScreen;
	this.type = type;
	renderWorld = gameScreen.renderWorld;
	this.x = x;
	this.y = y;
	setX(x);
	setY(y);
}

/**
 * Lazily returns a cache for this ammunitionType of wall. Each {@link WallType} has its own cache object.
 *
 * @return A new cache, or an existing one.
 */
private WallImageCache getCache() {
	WallImageCache wallImageCache = caches.get(type);
	if (wallImageCache == null) {
		wallImageCache = new WallImageCache(type, NUMBER_OF_SLOTS_IN_CACHE);
		caches.put(type, wallImageCache);
	}
	return wallImageCache;
}

/**
 * Computes a hash describing the image of this wall: which sides the wall faces, which transitions are applied to it.
 * Image hash is a sum of {@link WallImageCache} constant fields, which are bitmasks.
 *
 * @return A hash describing this wall.
 * @see WallImageCache
 */
public int getWallHash() {
	RenderCell cell = renderWorld.getCell(x, y);
	RenderCell cellFromSouth = renderWorld.getCell(x, y + 1);
	int imageHash = 0;
	if (cell.isVisible()) {
		imageHash += WallImageCache.VISIBLE;
	}
	if (cellFromSouth == null) {
		imageHash += WallImageCache.SOUTH_WALL_DARK;
	} else if (!cellFromSouth.hasWall() && cellFromSouth.isVisible()) {
		imageHash += WallImageCache.SOUTH_WALL_SHADE;
	}
	if (gameScreen.getCurrentBackendPlane().hasWall(x, y - 1)) {
		imageHash += WallImageCache.SIDE_N;
	}
	if (gameScreen.getCurrentBackendPlane().hasWall(x + 1, y)) {
		imageHash += WallImageCache.SIDE_E;
	}
	if (gameScreen.getCurrentBackendPlane().hasWall(x, y + 1)) {
		imageHash += WallImageCache.SIDE_S;
	}
	if (gameScreen.getCurrentBackendPlane().hasWall(x - 1, y)) {
		imageHash += WallImageCache.SIDE_W;
	}
	if (renderWorld.getCell(x, y - 1) == null) {
		imageHash += WallImageCache.DARK_N;
	} else if (renderWorld.getCell(x, y - 1).isVisible()) {
		imageHash += WallImageCache.SHADE_N;
	}
	if (renderWorld.getCell(x + 1, y) == null) {
		imageHash += WallImageCache.DARK_E;
	} else if (renderWorld.getCell(x + 1, y).isVisible()) {
		imageHash += WallImageCache.SHADE_E;
	}
	if (renderWorld.getCell(x, y + 1) == null) {
		imageHash += WallImageCache.DARK_S;
	} else if (renderWorld.getCell(x, y + 1).isVisible()) {
		imageHash += WallImageCache.SHADE_S;
	}
	if (renderWorld.getCell(x - 1, y) == null) {
		imageHash += WallImageCache.DARK_W;
	} else if (renderWorld.getCell(x - 1, y).isVisible()) {
		imageHash += WallImageCache.SHADE_W;
	}
	boolean hasSouthCellButNotWall = renderWorld.hasCell(x, y + 1)
		&& !renderWorld.getCell(x, y + 1).hasWall();
	if (hasSouthCellButNotWall) {
		if (!renderWorld.hasCell(x - 1, y)) {
			imageHash += WallImageCache.DARK_SOUTH_WALL_LEFT;
		} else if (cell.isVisible() && renderWorld.hasCell(x - 1, y)
			&& !renderWorld.getCell(x - 1, y).isVisible()) {
			imageHash += WallImageCache.SHADE_SOUTH_WALL_LEFT;
		}
	}
	if (hasSouthCellButNotWall) {
		if (!renderWorld.hasCell(x + 1, y)) {
			imageHash += WallImageCache.DARK_SOUTH_WALL_RIGHT;
		} else if (cell.isVisible() && renderWorld.hasCell(x + 1, y)
			&& !renderWorld.getCell(x + 1, y).isVisible()) {
			imageHash += WallImageCache.SHADE_SOUTH_WALL_RIGHT;
		}
	}
	return imageHash;
}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean inScreenRectangle = gameScreen.isInScreenRectangle(
		x,
		y,
		gameScreen.startCellX,
		gameScreen.startCellY,
		gameScreen.windowWidthCells,
		gameScreen.windowHeightCells + 1
	);
	if (!inScreenRectangle) {
		// Cull those walls that aren't inside viewport.
		return;
	}
	WallImageCache cache = getCache();
	RenderCell cell = renderWorld.getCell(x, y);
	RenderCell cellFromSouth = renderWorld.getCell(x, y + 1);
	int imageHash = getWallHash();
	if (!cache.hasImage(imageHash)) {
		gameScreen.postProcessor.captureEnd();
		batch.end();
		gameScreen.postProcessor.captureNoClear();
		generateImage(cell, cellFromSouth, imageHash, cache);
		batch.begin();
		// No batch.end() because it is an actor
	}
	TextureRegion image = cache.getImage(imageHash);
	batch.draw(
		image,
		x * GameScreen.TILE_SIZE,
		y * GameScreen.TILE_SIZE - wallHeights.get(type) + GameScreen.TILE_SIZE);
}

/**
 * Draws in image of a wall with all transitions above it and caches that image into a {@link WallImageCache}.
 *
 * @param cell
 * 	A cell where the wall resides.
 * @param cellFromSouth
 * 	A cell 1 unit to south from {@code cell}.
 * @param imageHash
 * 	{@see WallImageCache}.
 * @param cache
 * 	A cache where to save the wall image after it is drawn.
 */
private void generateImage(RenderCell cell, RenderCell cellFromSouth, int imageHash, WallImageCache cache) {
	// There is a complexity in drawing walls: drawing transitions above walls.
	// These transitions mostly go on the "roof" of a wall, i.e. higher than floor transitions.
	depthTestFrameBuffer.begin();
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_ALWAYS);

	// Draw visible walls.
	// For each opaque fragment of a visible wall, we place a 0 to the depth buffer.
	// This will later indicate a mask for drawing transitions on walls.
	depthTestBatch.setShader(writeOpaqueToDepthShader);
	depthTestBatch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	// SpriteBatch disables depth buffer with glDepthMask(false) internally,
	// so we have to re-enable it to properly write our depth mask.
	Gdx.gl.glDepthMask(true);
	TextureRegion wall = getWallTextureByCell(x, y);
	int wallTextureHeight = wall.getRegionHeight();
	if (cell.isVisible()) {
		depthTestBatch.draw(wall, 0, 0);
		if (cellFromSouth != null && !cellFromSouth.isVisible()) {
			if (!cellFromSouth.hasWall()) {
				// Draw shaded south front faces of unseen walls that don't have a wall neighbor from south.
				// For that we'll need to update the depth mask from scratch, so we clear depth buffer to 1.0.
				// It will consist only of rectangles covering those wall sides.
				depthTestBatch.setShader(drawOpaqueToDepth05Shader);
				int wallSideHeight = wallTextureHeight - GameScreen.TILE_SIZE;
				int origY = wall.getRegionY();
				int origX = wall.getRegionX();
				// For drawing the south side of a wall we temporarily set wall's texture region
				// to cover only that part of wall...
				wall.setRegion(origX, origY, GameScreen.TILE_SIZE, -wallSideHeight);
				Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
				depthTestBatch.draw(
					wall,
					0,
					32
				);
				Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
				// ...and then restore it back.
				wall.setRegion(origX, origY, GameScreen.TILE_SIZE, -wallTextureHeight);
				depthTestBatch.setShader(writeOpaqueToDepthShader);
			}
		}
	}
	depthTestBatch.end();

	// Draw unseen walls
	depthTestBatch.setShader(drawWithDepth0Shader);
	depthTestBatch.begin();
	Gdx.gl.glDepthMask(true);
	if (!cell.isVisible()) {
		depthTestBatch.draw(wall, 0, 0);
	}
	depthTestBatch.end();

	// Create mask for FOV transitions above walls
	Gdx.gl.glDepthFunc(GL10.GL_GREATER);
	depthTestBatch.setShader(drawOpaqueToDepth05Shader);
	depthTestBatch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glColorMask(false, false, false, false);
	Gdx.gl.glDepthMask(true);
	drawDepthMaskAndOpaqueTransitionOnWall(x, y, cell);
	depthTestBatch.end();

	// Draw seen walls again above the 0.5 depth mask, but now with rgb *= 0.6 so masked pixels appear darker
	Gdx.gl.glColorMask(true, true, true, true);
	Gdx.gl.glDepthFunc(GL10.GL_EQUAL);
	depthTestBatch.setShader(GameScreen.drawWithRGB06Shader);
	depthTestBatch.begin();
	if (cell.isVisible()) {
		depthTestBatch.draw(wall, 0, 0);
	}
	depthTestBatch.end();

	Gdx.gl.glDepthMask(false);
	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);

	depthTestBatch.setShader(GameScreen.defaultShader);

	depthTestFrameBuffer.end();
	cache.putImage(imageHash, depthTestFrameBuffer);
}

/**
 * For a transition where there is no neighbor from that side, draws an opaque black transition; for a transition to an
 * unseen neighbor cell, draws a mask for an upcoming darkened transition.
 *
 * @param x
 * 	Absolute x coordinate of a cell.
 * @param y
 * 	Absolute y coordinate of a cell.
 * @param cell
 * 	The cell to draw at.
 */
void drawDepthMaskAndOpaqueTransitionOnWall(int x, int y, RenderCell cell) {
	int wallHeight = getWallHeight((WallType) cell.getObject());
	for (CardinalDirection dir : CardinalDirection.values()) {
		// Here to get texture number shift we pass absolute coordinates x and y, because,
		// unlike in applyUnseenBrightnessMap(),  here position of transition in not relative to viewport.
		int[] d = dir.side2d();
		RenderCell neighborCell = renderWorld.getCell(x + d[0], y + d[1]);
		TextureRegion transition = null;
		boolean noNeighbor = neighborCell == null;
		if (noNeighbor) {
			// Drawing black pixels for transitions to not yet seen cells on wall's height
			// (right into color buffer, hence trueing color mask).
			Gdx.gl.glColorMask(true, true, true, true);
			Gdx.gl.glDepthMask(false);
			transition = fovEdgeOnWallToNotYetSeen.getTransition(dir, x, y);
		} else if (cell.isVisible() && !neighborCell.isVisible()) {
			// Draw mask for transitions to unseen walls.
			Gdx.gl.glColorMask(false, false, false, false);
			Gdx.gl.glDepthMask(true);
			transition = fovEdgeOnWallToUnseen.getTransition(dir, x, y);
		} else if (dir.isHorizontal()
			&& renderWorld.hasCell(x, y + 1)
			&& !renderWorld.getCell(x, y + 1).hasWall()
			) {
			// Draw transitions just on south side of a wall in case where a neighbor wall is visible, but there should
			// be a transition because a cell below it is not.
			if (renderWorld.hasCell(x + d[0], y + 1)
				&& neighborCell.isVisible()
				&& cell.isVisible()
				&& renderWorld.hasCell(x - d[0], y)
				&& !renderWorld.getCell(x - d[0], y).hasWall()
				) {
				if (!renderWorld.getCell(x + d[0], y + 1).isVisible()) {
					// Draw mask for transitions to unseen south wall sides.
					depthTestBatch.setShader(drawOpaqueToDepth05Shader);
					Gdx.gl.glColorMask(false, false, false, false);
					Gdx.gl.glDepthMask(true);
					depthTestBatch.draw(fovEdgeOnWallToNotYetSeen.getTransition(dir, x, y), 0, 0);
					depthTestBatch.setShader(drawOpaqueToDepth05Shader);
				}
			} else if (!gameScreen.isFloorUnderWallShouldBeDrawn(x + d[0], y)) {
				// Draw black color for transitions to not yet seen south wall sides.
				Gdx.gl.glColorMask(true, true, true, true);
				Gdx.gl.glDepthMask(true);
				Gdx.gl.glDepthFunc(GL10.GL_LESS);
				depthTestBatch.setShader(opaque0Transparent05DepthShader);
				depthTestBatch.draw(fovEdgeOnWallToNotYetSeen.getTransition(dir, x, y), 0, 0);
				depthTestBatch.setShader(drawOpaqueToDepth05Shader);
				Gdx.gl.glDepthFunc(GL10.GL_GREATER);
				Gdx.gl.glColorMask(false, false, false, false);
				Gdx.gl.glDepthMask(true);
			}
		}
		if (transition != null) {
			if (dir.isHorizontal()
				&& renderWorld.hasCell(x, y + 1)
				&& !renderWorld.getCell(x, y + 1).hasWall()
				) {
				// If this wall has a visible south side,
				// draw transitions on the lower part of a wall too.
				depthTestBatch.draw(transition, 0, wallHeight - GameScreen.TILE_SIZE);
			}
			depthTestBatch.draw(transition, 0, 0);
			depthTestBatch.end();
			Gdx.gl.glColorMask(false, false, false, false);
			Gdx.gl.glDepthMask(true);
			depthTestBatch.begin();
		}
	}
}

/**
 * Returns image of a wall without any transitions drawn over it.
 *
 * @param x
 * 	X coordinate of a cell in world coordinates.
 * @param y
 * 	Y coordinate of a cell in world coordinates.
 * @return
 */
TextureRegion getWallTextureByCell(int x, int y) {
	WallType wallType = (WallType) gameScreen.getCurrentBackendPlane().getGameObject(x, y);
	String name = wallType.getResourceName();
	int index = 0;
	RenderCell neighborCell = gameScreen.renderWorld.getCell(x, y - 1);
	if (neighborCell == null || neighborCell.getObject() == wallType) {
		index += 1000;
	}
	neighborCell = gameScreen.renderWorld.getCell(x + 1, y);
	if (neighborCell == null || neighborCell.getObject() == wallType) {
		index += 100;
	}
	neighborCell = gameScreen.renderWorld.getCell(x, y + 1);
	if (neighborCell == null || neighborCell.getObject() == wallType) {
		index += 10;
	}
	neighborCell = gameScreen.renderWorld.getCell(x - 1, y);
	if (neighborCell == null || neighborCell.getObject() == wallType) {
		index += 1;
	}
	return AtlasWalls.getInstance().findRegion(name, index);
}

int getWallHeight(WallType type) {
	return wallHeights.get(type);
}
}
