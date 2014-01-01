package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
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
static final ShaderProgram writeOpaqueToDepthShader = GameScreen.createShader(Gdx.files.internal("shaders/writeOpaqueToDepth.f.glsl"));
static final ShaderProgram drawOpaqueToDepth05Shader = new ShaderProgram(GameScreen.defaultShader.getVertexShaderSource(), Gdx.files.internal("shaders/drawOpaqueToDepth05.glsl").readString());
static final ShaderProgram drawWithDepth0Shader = GameScreen.createShader(Gdx.files.internal("shaders/drawWithDepth0.f.glsl"));
static final ShaderProgram opaque0Transparent05DepthShader = GameScreen.createShader(Gdx.files.internal("shaders/opaque0transparent05depth.f.glsl"));
static final TransitionPregenerator fovEdgeOnWallToUnseen = new FovEdgeTransparent();
static final TransitionPregenerator fovEdgeOnWallToNotYetSeen = new FovEdgeOpaque();
private static final TextureAtlas atlasWalls = new TextureAtlas(Gdx.files.internal("pack/walls.atlas"), true);
private static final Map<String, Integer> wallHeights;

static {
	Array<TextureAtlas.AtlasRegion> regions = atlasWalls.getRegions();
	int numberOfWallTypes = regions.size;
	wallHeights = new HashMap<>(numberOfWallTypes);
	for (TextureAtlas.AtlasRegion region : regions) {
		wallHeights.put(region.name, region.getRegionHeight());
	}
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
}

@Override
public void draw(Batch batch, float parentAlpha) {
	if (!gameScreen.isOnScreen(x, y)) {
		// Cull those walls that aren't inside viewport.
		return;
	}
	gameScreen.postProcessor.captureEnd();
	batch.end();
	// There is a complexity in drawing walls: drawing transitions above walls.
	// These transitions mostly go on the "roof" of a wall, i.e. higher than floor transitions.
	RenderCell cell = renderWorld.getCell(x, y);
	gameScreen.depthTestFrameBuffer.begin();
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_ALWAYS);

	// Draw visible walls.
	// For each opaque fragment of a visible wall, we place a 0 to the depth buffer.
	// This will later indicate a mask for drawing transitions on walls.
	batch.setShader(writeOpaqueToDepthShader);
	batch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	// SpriteBatch disables depth buffer with glDepthMask(false) internally,
	// so we have to re-enable it to properly write our depth mask.
	Gdx.gl.glDepthMask(true);
	if (cell.isVisible()) {
		TextureRegion wall = getWallTextureByCell(x, y);
		int wallTextureHeight = wall.getRegionHeight();
		batch.draw(wall, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - (wallTextureHeight - GameScreen.TILE_SIZE));
		RenderCell cellFromSouth = renderWorld.getCell(x, y + 1);
		if (cellFromSouth != null && !cellFromSouth.isVisible()) {
			if (!cellFromSouth.hasWall()) {
				// Draw shaded south front faces of unseen walls that don't have a wall neighbor from south.
				// For that we'll need to update the depth mask from scratch, so we clear depth buffer to 1.0.
				// It will consist only of rectangles covering those wall sides.
				batch.setShader(drawOpaqueToDepth05Shader);
				int wallSideHeight = wallTextureHeight - GameScreen.TILE_SIZE;
				int origY = wall.getRegionY();
				int origX = wall.getRegionX();
				// For drawing the south side of a wall we temporarily set wall's texture region
				// to cover only that part of wall...
				wall.setRegion(origX, origY, GameScreen.TILE_SIZE, -wallSideHeight);
				Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
				batch.draw(
					wall,
					x * GameScreen.TILE_SIZE,
					y * GameScreen.TILE_SIZE + GameScreen.TILE_SIZE - wallTextureHeight + GameScreen.TILE_SIZE,
					GameScreen.TILE_SIZE,
					wallSideHeight
				);
				Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
				// ...and then restore it back.
				wall.setRegion(origX, origY, GameScreen.TILE_SIZE, -wallTextureHeight);
				batch.setShader(writeOpaqueToDepthShader);
			}
		}
	}
	batch.end();

	// Draw unseen walls
	batch.setShader(drawWithDepth0Shader);
	batch.begin();
	Gdx.gl.glDepthMask(true);
	if (!cell.isVisible()) {
		TextureRegion wall = getWallTextureByCell(x, y);
		batch.draw(wall, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - (wall.getRegionHeight() - GameScreen.TILE_SIZE));
	}
	batch.end();

	// Create mask for FOV transitions above walls
	Gdx.gl.glDepthFunc(GL10.GL_GREATER);
	batch.setShader(drawOpaqueToDepth05Shader);
	batch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glColorMask(false, false, false, false);
	Gdx.gl.glDepthMask(true);
	drawDepthMaskAndOpaqueTransitionOnWall(batch, x, y, cell);
	batch.end();

	// Draw seen walls again above the 0.5 depth mask, but now with rgb *= 0.6 so masked pixels appear darker
	Gdx.gl.glColorMask(true, true, true, true);
	Gdx.gl.glDepthFunc(GL10.GL_EQUAL);
	batch.setShader(GameScreen.drawWithRGB06Shader);
	batch.begin();
	if (cell.isVisible()) {
		TextureRegion wall = getWallTextureByCell(x, y);
		int wallTextureHeight = wall.getRegionHeight();
		batch.draw(wall, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - (wallTextureHeight - GameScreen.TILE_SIZE));
	}
	batch.end();

	Gdx.gl.glDepthMask(false);
	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);

	batch.setShader(GameScreen.defaultShader);

	gameScreen.depthTestFrameBuffer.end();
	gameScreen.postProcessor.captureNoClear();

	batch.begin();
	batch.draw(gameScreen.depthTestFrameBuffer.getColorBufferTexture(), gameScreen.startPixelX, gameScreen.startPixelY);
	// No batch.end() because it is an actor
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
void drawDepthMaskAndOpaqueTransitionOnWall(Batch batch, int x, int y, RenderCell cell) {
	int wallHeight = getWallHeight(cell.getWall());
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
					batch.setShader(drawOpaqueToDepth05Shader);
					Gdx.gl.glColorMask(false, false, false, false);
					Gdx.gl.glDepthMask(true);
					batch.draw(fovEdgeOnWallToUnseen.getTransition(dir, x, y), x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
					batch.setShader(drawOpaqueToDepth05Shader);
				}
			} else if (!gameScreen.isFloorUnderWallShouldBeDrawn(x + d[0], y)) {
				// Draw black color for transitions to not yet seen south wall sides.
				Gdx.gl.glColorMask(true, true, true, true);
				Gdx.gl.glDepthMask(true);
				Gdx.gl.glDepthFunc(GL10.GL_LESS);
				batch.setShader(opaque0Transparent05DepthShader);
				batch.draw(fovEdgeOnWallToNotYetSeen.getTransition(dir, x, y), x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
				batch.setShader(drawOpaqueToDepth05Shader);
				Gdx.gl.glDepthFunc(GL10.GL_GREATER);
				Gdx.gl.glColorMask(false, false, false, false);
				Gdx.gl.glDepthMask(true);
			}
		}
		if (transition != null) {
			if (dir.isHorizontal() && renderWorld.hasCell(x, y + 1) && !renderWorld.getCell(x, y + 1).hasWall()) {
				// If this wall has a visible south side,
				// draw transitions on the lower part of a wall too.
				batch.draw(transition, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
			}
			batch.draw(transition, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - wallHeight + GameScreen.TILE_SIZE);
			batch.end();
			Gdx.gl.glColorMask(false, false, false, false);
			Gdx.gl.glDepthMask(true);
			batch.begin();
		}
	}
}

TextureRegion getWallTextureByCell(int x, int y) {
	WallType wallType = gameScreen.getCurrentBackendPlane().getWall(x, y);
	String name = wallType.getResourceName();
	int index = 0;
	RenderCell neighborCell = gameScreen.renderWorld.getCell(x, y - 1);
	if (neighborCell == null || neighborCell.getWall() == wallType) {
		index += 1000;
	}
	neighborCell = gameScreen.renderWorld.getCell(x + 1, y);
	if (neighborCell == null || neighborCell.getWall() == wallType) {
		index += 100;
	}
	neighborCell = gameScreen.renderWorld.getCell(x, y + 1);
	if (neighborCell == null || neighborCell.getWall() == wallType) {
		index += 10;
	}
	neighborCell = gameScreen.renderWorld.getCell(x - 1, y);
	if (neighborCell == null || neighborCell.getWall() == wallType) {
		index += 1;
	}
	return atlasWalls.findRegion(name, index);
}

int getWallHeight(WallType type) {
	return wallHeights.get(type.getResourceName());
}
}
