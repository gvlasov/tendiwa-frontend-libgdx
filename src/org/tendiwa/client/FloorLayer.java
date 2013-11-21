package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
private final SpriteBatch defaultBatch;
private final Map<CardinalDirection, Map<Integer, Pixmap>> floorTransitions = new HashMap<>();
private final int transitionsAtlasSize = 1024;
private final FrameBuffer transitionsFrameBuffer;
private final short[] floorsFrom4Sides = new short[4];
private Map<Integer, TextureRegion> floorRegions = new HashMap<>();
private Map<Short, Integer> floorIndices = new HashMap<>();
private TransitionsToFloor[] floorTransitionsProviders;
private Map<String, TextureRegion> transitionsMap = new HashMap<>();
private Texture bufTexture0 = new Texture(new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture1 = new Texture(new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture2 = new Texture(new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture3 = new Texture(new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888));

public FloorLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	atlasFloors = new TextureAtlas(Gdx.files.internal("pack/floors.atlas"), true);
	cacheRegions();
	defaultBatch = new SpriteBatch();
	transitionsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, gameScreen.windowWidth, gameScreen.windowHeight, false);
	initFloorTransitionsProviders();
}

void initFloorTransitionsProviders() {
	int size = FloorType.getAll().size();
	floorTransitionsProviders = new TransitionsToFloor[size];
	for (short i = 0; i < size; i++) {
		floorTransitionsProviders[i] = new TransitionsToFloor(i);
	}
}

void draw() {
	drawFloors();
	drawTransitions();
}

private void drawFloors() {
	int maxX = gameScreen.getMaxRenderCellX();
	int maxY = gameScreen.getMaxRenderCellY();
	gameScreen.batch.begin();
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			RenderCell cell = gameScreen.cells.get(x * gameScreen.WORLD.getHeight() + y);
			if (cell != null) {
				drawFloor(cell.getFloor(), x, y);
			}
		}
	}
	gameScreen.batch.end();
}

void drawFloor(short floorId, int x, int y) {
	if (!gameScreen.isFloorUnderWallShouldBeDrawn(x, y)) {
		// Don't draw floor on cells that are right under a wall on the edge of field of view,
		// because drawing one produces an unpleasant and unrealistic effect.
		return;
	}
	TextureRegion floor = getFloorTextureByCell(floorId, x, y);
	gameScreen.batch.draw(floor, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
}

private void drawTransitions() {
	// Draw transitions
	defaultBatch.setProjectionMatrix(gameScreen.camera.combined);
	defaultBatch.begin();
	for (int x = 0; x < gameScreen.windowWidth / GameScreen.TILE_SIZE; x++) {
		for (int y = 0; y < gameScreen.windowHeight / GameScreen.TILE_SIZE; y++) {
			RenderCell cell = gameScreen.cells.get((gameScreen.startCellX + x) * gameScreen.WORLD.getHeight() + (gameScreen.startCellY + y));
			// (!A || B) — see "Logical implication" in Wikipedia.
			// Shortly, if there is a wall, then floor under it should need to be drawn for a condition to pass.
			if (cell != null && (!cell.hasWall() || gameScreen.isFloorUnderWallShouldBeDrawn(gameScreen.startCellX + x, gameScreen.startCellY + y))) {
				drawFloorTransitionsInCell(cell);
			}
		}
	}
	defaultBatch.end();
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

//private Pixmap getTransition(CardinalDirection dir, int floorId) {
//	if (!floorTransitions.containsKey(dir)) {
//		floorTransitions.put(dir, new HashMap<Integer, Pixmap>());
//	}
//	if (!floorTransitions.get(dir).containsKey(floorId)) {
//		floorTransitions.get(dir).put(floorId, getFloorTransitionsProvider(floorId).getTransition(dir, 1, 1));
//	}
//	return floorTransitions.get(dir).get(floorId);
//}
//private TextureRegion createNewFloorTransitionRegion(int north, int east, int south, int west) {
//	String transitionKey = north + "_" + east + "_" + south + "_" + west;
//
//	Pixmap.Blending previousBlending = Pixmap.getBlending();
//	Pixmap.setBlending(Pixmap.Blending.None);
//	Pixmap transE = getTransition(Directions.E, east);
//	Pixmap transW = getTransition(Directions.W, west);
//	Pixmap transN = getTransition(Directions.N, north);
//	Pixmap transS = getTransition(Directions.S, south);
//	int atlasX = transitionsMap.size() * GameScreen.TILE_SIZE % transitionsAtlasSize;
//	int atlasY = transitionsMap.size() * GameScreen.TILE_SIZE / transitionsAtlasSize * GameScreen.TILE_SIZE;
//
//	Pixmap.setBlending(Pixmap.Blending.SourceOver);
//
//	bufTexture0.draw(transN, 0, 0);
//	bufTexture1.draw(transE, 0, 0);
//	bufTexture2.draw(transS, 0, 0);
//	bufTexture3.draw(transW, 0, 0);
//	transitionsFrameBuffer.begin();
//	defaultBatch.begin();
//	defaultBatch.draw(bufTexture0, atlasX, atlasY);
//	defaultBatch.draw(bufTexture1, atlasX, atlasY);
//	defaultBatch.draw(bufTexture2, atlasX, atlasY);
//	defaultBatch.draw(bufTexture3, atlasX, atlasY);
//	defaultBatch.end();
//	transitionsFrameBuffer.end();
//
//	TextureRegion textureRegion = new TextureRegion(transitionsFrameBuffer.getColorBufferTexture(), atlasX, atlasY, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
//	transitionsMap.put(transitionKey, textureRegion);
//
//	Pixmap.setBlending(previousBlending);
//	return textureRegion;
//}
private void drawCellWithTransitions(int x, int y, short self) {
	// Get individual transition pixmap for each side
	for (int i = 0; i < 4; i++) {
		if (floorsFrom4Sides[i] != self) {
			TransitionsToFloor floorTransitionsProvider = getFloorTransitionsProvider(floorsFrom4Sides[i]);
			defaultBatch.draw(
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

//private TextureRegion getTransition(int north, int east, int south, int west) {
//	String transitionKey = north + "_" + east + "_" + south + "_" + west;
//	if (transitionsMap.containsKey(transitionKey)) {
//		return transitionsMap.get(transitionKey);
//	} else {
//		return createNewFloorTransitionRegion(north, east, south, west);
//	}
//}
void drawFloorTransitionsInCell(RenderCell cell) {
	short self = cell.getFloor();
	RenderCell renderCell = gameScreen.cells.get(cell.getX() * gameScreen.WORLD.getHeight() + (cell.getY() + 1));
	// Indices 0 and 2 are swapped
	floorsFrom4Sides[2] = cell.getY() + 1 < gameScreen.worldHeightCells && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.cells.get((cell.getX() + 1) * gameScreen.WORLD.getHeight() + cell.getY());
	floorsFrom4Sides[1] = cell.getX() + 1 < gameScreen.worldWidthCells && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.cells.get(cell.getX() * gameScreen.WORLD.getHeight() + (cell.getY() - 1));
	floorsFrom4Sides[0] = cell.getY() > 0 && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.cells.get((cell.getX() - 1) * gameScreen.WORLD.getHeight() + cell.getY());
	floorsFrom4Sides[3] = cell.getX() > 0 && renderCell != null ? renderCell.getFloor() : self;
	if (floorsFrom4Sides[0] != self || floorsFrom4Sides[1] != self || floorsFrom4Sides[2] != self || floorsFrom4Sides[3] != self) {
		drawCellWithTransitions(cell.getX(), cell.getY(), self);
	}
}
}
