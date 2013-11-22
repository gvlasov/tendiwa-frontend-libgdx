package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import tendiwa.core.CardinalDirection;
import tendiwa.core.FloorType;
import tendiwa.core.RenderCell;
import tendiwa.core.Tendiwa;

import java.util.HashMap;
import java.util.Map;

public class FloorLayer {
private final GameScreen gameScreen;
private final TextureAtlas atlasFloors;
private final SpriteBatch batch;
private final int transitionsAtlasSize = 1024;
private final FrameBuffer transitionsFrameBuffer;
private final short[] floorsFrom4Sides = new short[4];
private final ShaderProgram liquidFloorAnimateShader;
private final int uWaveState;
private Map<Integer, TextureRegion> floorRegions = new HashMap<>();
private Map<Short, Integer> floorIndices = new HashMap<>();
private TransitionsToFloor[] floorTransitionsProviders;

public FloorLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	atlasFloors = new TextureAtlas(Gdx.files.internal("pack/floors.atlas"), true);
	cacheRegions();
	batch = new SpriteBatch();
	transitionsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, gameScreen.windowWidth, gameScreen.windowHeight, false);
	initFloorTransitionsProviders();
	liquidFloorAnimateShader = GameScreen.createShader(Gdx.files.internal("shaders/liquidFloorAnimate.f.glsl"));
	uWaveState = liquidFloorAnimateShader.getUniformLocation("waveState");
}

void initFloorTransitionsProviders() {
	int size = FloorType.getAll().size();
	floorTransitionsProviders = new TransitionsToFloor[size];
	for (short i = 0; i < size; i++) {
		floorTransitionsProviders[i] = new TransitionsToFloor(i);
	}
}

void draw() {
	liquidFloorAnimateShader.begin();
	liquidFloorAnimateShader.setUniformf(
		uWaveState, waveState(0.5f)
	);
	batch.setProjectionMatrix(gameScreen.camera.combined);
	batch.begin();
	drawFloors(false);
	batch.setShader(liquidFloorAnimateShader);
	drawFloors(true);
	batch.setShader(GameScreen.defaultShader);
	drawTransitions(false);
	batch.setShader(liquidFloorAnimateShader);
	drawTransitions(true);
	batch.end();
	batch.setShader(GameScreen.defaultShader);
}

private float waveState(float frequency) {
	return (float) ((Math.PI*2 /2000*frequency)*System.currentTimeMillis()%(Math.PI*2));
}

private void drawFloors(boolean liquid) {
	int maxX = gameScreen.getMaxRenderCellX();
	int maxY = gameScreen.getMaxRenderCellY();
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			RenderCell cell = gameScreen.cells.get(x * gameScreen.backendWorld.getHeight() + y);
			if (cell != null && FloorType.getById(cell.getFloor()).isLiquid() == liquid) {
				drawFloor(cell.getFloor(), x, y);
			}
		}
	}
}

void drawFloor(short floorId, int x, int y) {
	if (!gameScreen.isFloorUnderWallShouldBeDrawn(x, y)) {
		// Don't draw floor on cells that are right under a wall on the edge of field of view,
		// because drawing one produces an unpleasant and unnatural effect.
		return;
	}
	TextureRegion floor = getFloorTextureByCell(floorId, x, y);
	batch.draw(floor, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
}

private void drawTransitions(boolean liquid) {
	// Draw transitions
	for (int x = 0; x < gameScreen.windowWidth / GameScreen.TILE_SIZE; x++) {
		for (int y = 0; y < gameScreen.windowHeight / GameScreen.TILE_SIZE; y++) {
			RenderCell cell = gameScreen.cells.get((gameScreen.startCellX + x) * gameScreen.backendWorld.getHeight() + (gameScreen.startCellY + y));
			// (!A || B) — see "Logical implication" in Wikipedia.
			// Shortly, if there is a wall, then floor under it should need to be drawn for a condition to pass.
			if (cell != null
				&& (!cell.hasWall() || gameScreen.isFloorUnderWallShouldBeDrawn(gameScreen.startCellX + x, gameScreen.startCellY + y))
				) {
				drawFloorTransitionsInCell(cell, liquid);
			}
		}
	}
}

private TextureRegion getFloorTextureByCell(short terrain, int x, int y) {
	int floorId = (int) terrain;
	int key = (int) ((floorId * (1 << 15)) + Math.round(Math.abs(Math.sin(x * y)) * floorIndices.get((short) floorId)));
	return floorRegions.get(key);
}

private void cacheRegions() {
	Map<String, Short> floorTypeName2id = new HashMap<>();
	Map<String, Array<TextureAtlas.AtlasRegion>> name2regions = new HashMap<>();
	for (Map.Entry<Short, FloorType> e : FloorType.getAll().entrySet()) {
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

private TransitionsToFloor getFloorTransitionsProvider(short floorId) {
	return floorTransitionsProviders[floorId];
}

void drawFloorTransitionsInCell(RenderCell cell, boolean liquid) {
	short self = cell.getFloor();
	RenderCell renderCell = gameScreen.cells.get(cell.getX() * gameScreen.backendWorld.getHeight() + (cell.getY() + 1));
	// Indices 0 and 2 are swapped
	floorsFrom4Sides[2] = cell.getY() + 1 < gameScreen.worldHeightCells && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.cells.get((cell.getX() + 1) * gameScreen.backendWorld.getHeight() + cell.getY());
	floorsFrom4Sides[1] = cell.getX() + 1 < gameScreen.worldWidthCells && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.cells.get(cell.getX() * gameScreen.backendWorld.getHeight() + (cell.getY() - 1));
	floorsFrom4Sides[0] = cell.getY() > 0 && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.cells.get((cell.getX() - 1) * gameScreen.backendWorld.getHeight() + cell.getY());
	floorsFrom4Sides[3] = cell.getX() > 0 && renderCell != null ? renderCell.getFloor() : self;
	if (floorsFrom4Sides[0] != self || floorsFrom4Sides[1] != self || floorsFrom4Sides[2] != self || floorsFrom4Sides[3] != self) {
		drawCellWithTransitions(cell.getX(), cell.getY(), self, liquid);
	}
}

private void drawCellWithTransitions(int x, int y, short self, boolean liquid) {
	// Get individual transition pixmap for each side
	for (CardinalDirection dir : CardinalDirection.values()) {
		int[] d = dir.side2d();
		int i = dir.getCardinalIndex();
		if (floorsFrom4Sides[i] != self
			&& FloorType.getById(gameScreen.getCell(x + d[0], y + d[1]).getFloor()).isLiquid() == liquid
			) {
			TransitionsToFloor floorTransitionsProvider = getFloorTransitionsProvider(floorsFrom4Sides[i]);
			batch.draw(
				floorTransitionsProvider.getTransition(
					i,
					x,
					y
				),
				x * GameScreen.TILE_SIZE,
				y * GameScreen.TILE_SIZE
			);
		}
	}
}
}
