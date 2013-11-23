package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import tendiwa.core.CardinalDirection;
import tendiwa.core.RenderCell;
import tendiwa.core.WallType;

/**
 * This class renders walls and field of view transitions above walls. I separated it from other render code because
 * there is a lot of spaghetti code concerning walls and I don't know how to get rid of it.
 */
public class WallsLayer {
final GameScreen gameScreen;
final TransitionPregenerator fovEdgeOnWallToUnseen;
final TransitionPregenerator fovEdgeOnWallToNotYetSeen;
static final ShaderProgram writeOpaqueToDepthShader = GameScreen.createShader(Gdx.files.internal("shaders/writeOpaqueToDepth.f.glsl"));
static final ShaderProgram drawOpaqueToDepth05Shader = new ShaderProgram(GameScreen.defaultShader.getVertexShaderSource(), Gdx.files.internal("shaders/drawOpaqueToDepth05.glsl").readString());
static final ShaderProgram drawWithDepth0Shader = GameScreen.createShader(Gdx.files.internal("shaders/drawWithDepth0.f.glsl"));
static final ShaderProgram drawWithRGB06Shader = GameScreen.createShader(Gdx.files.internal("shaders/drawWithRGB06.f.glsl"));
static final ShaderProgram opaque0Transparent05DepthShader = GameScreen.createShader(Gdx.files.internal("shaders/opaque0transparent05depth.f.glsl"));
final private TextureAtlas atlasWalls;
private final int[] wallHeights;

WallsLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	fovEdgeOnWallToUnseen = new FovEdgeTransparent();
	fovEdgeOnWallToNotYetSeen = new FovEdgeOpaque();
	atlasWalls = new TextureAtlas(Gdx.files.internal("pack/walls.atlas"), true);
	int numberOfWallTypes = WallType.getNumberOfWallTypes();
	wallHeights = new int[numberOfWallTypes];
	for (int i = 0; i < numberOfWallTypes; i++) {
		wallHeights[i] = atlasWalls.findRegion(WallType.getById(i + 1).getName()).getRegionHeight();
	}
}

void draw() {
	// There is a complexity in drawing walls: drawing transitions above walls.
	// These transitions mostly go on the "roof" of a wall, i.e. higher than floor transitions.
	gameScreen.depthTestFrameBuffer.begin();
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClearDepthf(1.0f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glDepthFunc(GL10.GL_ALWAYS);

	int maxX = gameScreen.getMaxRenderCellX();
	int maxY = gameScreen.getMaxRenderCellY() + 1; // +1 here is to draw walls that start below viewport

	// Draw visible walls.
	// For each opaque fragment of a visible wall, we place a 0 to the depth buffer.
	// This will later indicate a mask for drawing transitions on walls.
	gameScreen.batch.setShader(writeOpaqueToDepthShader);
	gameScreen.batch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	// SpriteBatch disables depth buffer with glDepthMask(false) internally,
	// so we have to re-enable it to properly write our depth mask.
	Gdx.gl.glDepthMask(true);
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			RenderCell cell = gameScreen.getCell(x, y);
			if (cell != null && cell.isVisible()) {
				if (cell.hasWall()) {
					TextureRegion wall = getWallTextureByCell(x, y);
					int wallTextureHeight = wall.getRegionHeight();
					gameScreen.batch.draw(wall, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - (wallTextureHeight - GameScreen.TILE_SIZE));
					RenderCell cellFromSouth = gameScreen.getCell(x, y + 1);
					if (cellFromSouth != null && !cellFromSouth.isVisible()) {
						if (cell.hasWall() && !cellFromSouth.hasWall()) {
							// Draw shaded south front faces of unseen walls that don't have a wall neighbor from south.
							// For that we'll need to update the depth mask from scratch, so we clear depth buffer to 1.0.
							// It will consist only of rectangles covering those wall sides.
							gameScreen.batch.setShader(drawOpaqueToDepth05Shader);
							int wallSideHeight = wallTextureHeight - GameScreen.TILE_SIZE;
							int origY = wall.getRegionY();
							int origX = wall.getRegionX();
							// For drawing the south side of a wall we temporarily set wall's texture region
							// to cover only that part of wall...
							wall.setRegion(origX, origY, GameScreen.TILE_SIZE, -wallSideHeight);
							Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
							gameScreen.batch.draw(
								wall,
								x * GameScreen.TILE_SIZE,
								y * GameScreen.TILE_SIZE + GameScreen.TILE_SIZE - wallTextureHeight + gameScreen.TILE_SIZE,
								GameScreen.TILE_SIZE,
								wallSideHeight
							);
							Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
							// ...and then restore it back.
							wall.setRegion(origX, origY, GameScreen.TILE_SIZE, -wallTextureHeight);
							gameScreen.batch.setShader(writeOpaqueToDepthShader);
						}
					}
				}
			}
		}
	}
	gameScreen.batch.end();

	// Draw unseen walls
	gameScreen.batch.setShader(drawWithDepth0Shader);
	gameScreen.batch.begin();
	Gdx.gl.glDepthMask(true);
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			RenderCell cell = gameScreen.getCell(x, y);
			if (cell != null && !cell.isVisible()) {
				if (cell.hasWall()) {
					TextureRegion wall = getWallTextureByCell(x, y);
					gameScreen.batch.draw(wall, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - (wall.getRegionHeight() - GameScreen.TILE_SIZE));
				}
			}
		}
	}
	gameScreen.batch.end();

	// Create mask for FOV transitions above walls
	Gdx.gl.glDepthFunc(GL10.GL_GREATER);
	gameScreen.batch.setShader(drawOpaqueToDepth05Shader);
	gameScreen.batch.begin();
	Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	Gdx.gl.glColorMask(false, false, false, false);
	Gdx.gl.glDepthMask(true);
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			RenderCell cell = gameScreen.getCell(x, y);
			if (cell != null) {
				if (cell.hasWall()) {
					gameScreen.wallsLayer.drawDepthMaskAndOpaqueTransitionOnWall(x, y, cell);
				}
			}
		}
	}
	gameScreen.batch.end();

	// Draw seen walls again above the 0.5 depth mask, but now with rgb *= 0.6 so masked pixels appear darker
	Gdx.gl.glColorMask(true, true, true, true);
	Gdx.gl.glDepthFunc(GL10.GL_EQUAL);
	gameScreen.batch.setShader(drawWithRGB06Shader);
	gameScreen.batch.begin();
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			RenderCell cell = gameScreen.getCell(x, y);
			if (cell != null && cell.isVisible()) {
				if (cell.hasWall()) {
					TextureRegion wall = getWallTextureByCell(x, y);
					int wallTextureHeight = wall.getRegionHeight();
					gameScreen.batch.draw(wall, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - (wallTextureHeight - GameScreen.TILE_SIZE));
				}
			}
		}
	}
	gameScreen.batch.end();

	Gdx.gl.glDepthMask(false);
	Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);

	gameScreen.batch.setShader(GameScreen.defaultShader);

	gameScreen.depthTestFrameBuffer.end();

	gameScreen.batch.begin();
	gameScreen.batch.draw(gameScreen.depthTestFrameBuffer.getColorBufferTexture(), gameScreen.startPixelX, gameScreen.startPixelY);
	gameScreen.batch.end();

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
	int wallHeight = getWallHeight(cell.getFloor());
	for (CardinalDirection dir : CardinalDirection.values()) {
		// Here to get texture number shift we pass absolute coordinates x and y, because,
		// unlike in applyUnseenBrightnessMap(),  here position of transition in not relative to viewport.
		int[] d = dir.side2d();
		RenderCell neighborCell = gameScreen.getCell(x + d[0], y + d[1]);
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
			&& gameScreen.hasCell(x, y + 1)
			&& !gameScreen.getCell(x, y + 1).hasWall()
			) {
			// Draw transitions just on south side of a wall in case where a neighbor wall is visible, but there should
			// be a transition because a cell below it is not.
			if (gameScreen.hasCell(x + d[0], y + 1)
				&& neighborCell.isVisible()
				&& cell.isVisible()
				&& gameScreen.hasCell(x - d[0], y)
				&& !gameScreen.getCell(x - d[0], y).hasWall()
				) {
				if (!gameScreen.getCell(x + d[0], y + 1).isVisible()) {
					// Draw mask for transitions to unseen south wall sides.
					gameScreen.batch.setShader(drawOpaqueToDepth05Shader);
					Gdx.gl.glColorMask(false, false, false, false);
					Gdx.gl.glDepthMask(true);
					gameScreen.batch.draw(fovEdgeOnWallToUnseen.getTransition(dir, x, y), x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
					gameScreen.batch.setShader(drawOpaqueToDepth05Shader);
				}
			} else if (!gameScreen.isFloorUnderWallShouldBeDrawn(x + d[0], y)) {
				// Draw black color for transitions to not yet seen south wall sides.
				Gdx.gl.glColorMask(true, true, true, true);
				Gdx.gl.glDepthMask(true);
				Gdx.gl.glDepthFunc(GL10.GL_LESS);
				gameScreen.batch.setShader(opaque0Transparent05DepthShader);
				gameScreen.batch.draw(fovEdgeOnWallToNotYetSeen.getTransition(dir, x, y), x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
				gameScreen.batch.setShader(drawOpaqueToDepth05Shader);
				Gdx.gl.glDepthFunc(GL10.GL_GREATER);
				Gdx.gl.glColorMask(false, false, false, false);
				Gdx.gl.glDepthMask(true);
			}
		}
		if (transition != null) {
			if (dir.isHorizontal() && gameScreen.hasCell(x, y + 1) && !gameScreen.getCell(x, y + 1).hasWall()) {
				// If this wall has a visible south side,
				// draw transitions on the lower part of a wall too.
				gameScreen.batch.draw(transition, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
			}
			gameScreen.batch.draw(transition, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE - wallHeight + GameScreen.TILE_SIZE);
			gameScreen.batch.end();
			Gdx.gl.glColorMask(false, false, false, false);
			Gdx.gl.glDepthMask(true);
			gameScreen.batch.begin();
		}
	}
}

TextureRegion getWallTextureByCell(int x, int y) {
	short wallId = gameScreen.cells.get(x * gameScreen.backendWorld.getHeight() + y).getWall();
	WallType wallType = WallType.getById(wallId);
	String name = wallType.getName();
	int index = 0;
	RenderCell neighborCell = gameScreen.cells.get(x * gameScreen.backendWorld.getHeight() + (y - 1));
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 1000;
	}
	neighborCell = gameScreen.cells.get((x + 1) * gameScreen.backendWorld.getHeight() + y);
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 100;
	}
	neighborCell = gameScreen.cells.get(x * gameScreen.backendWorld.getHeight() + (y + 1));
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 10;
	}
	neighborCell = gameScreen.cells.get((x - 1) * gameScreen.backendWorld.getHeight() + y);
	if (neighborCell == null || neighborCell.getWall() == wallId) {
		index += 1;
	}
	return atlasWalls.findRegion(name, index);
}

int getWallHeight(short terrain) {
	return wallHeights[terrain];
}
}
