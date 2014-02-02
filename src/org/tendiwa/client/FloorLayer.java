package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import org.tendiwa.core.*;
import org.tendiwa.groovy.Registry;

import java.util.HashMap;
import java.util.Map;

public class FloorLayer {
private final EnhancedRectangle world;
private final GameScreen gameScreen;
private final GameScreenViewport viewport;
private final TextureAtlas atlasFloors;
private final SpriteBatch batch;
private final int transitionsAtlasSize = 1024;
private final FrameBuffer transitionsFrameBuffer;
private final FloorType[] floorsFrom4Sides = new FloorType[4];
private final ShaderProgram liquidFloorAnimateShader;
private final int uWaveState;
private Map<FloorType, Map<Integer, TextureRegion>> floorRegions = new HashMap<>();
private Map<FloorType, Integer> floorIndices = new HashMap<>();
private Map<FloorType, TransitionsToFloor> floorTransitionsProviders;
private boolean animateLiquidFloor;

public FloorLayer(EnhancedRectangle world, GameScreen gameScreen, GameScreenViewport viewport) {
	this.world = world;
	this.gameScreen = gameScreen;
	this.viewport = viewport;
	atlasFloors = new TextureAtlas(Gdx.files.internal("pack/floors.atlas"), true);
	cacheRegions();
	batch = new SpriteBatch();
	transitionsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getWindowWidthPixels(), viewport.getWindowHeightPixels(), false);
	initFloorTransitionsProviders();
	liquidFloorAnimateShader = GameScreen.createShader(Gdx.files.internal("shaders/liquidFloorAnimate.f.glsl"));
	uWaveState = liquidFloorAnimateShader.getUniformLocation("waveState");
}

void initFloorTransitionsProviders() {
	floorTransitionsProviders = new HashMap<>();
	for (FloorType floorType : Registry.floorTypes) {
		floorTransitionsProviders.put(floorType, new TransitionsToFloor(floorType));
	}
}

void draw() {
	// Config is read once per frame
	animateLiquidFloor = gameScreen.getConfig().animateLiquidFloor;
	if (animateLiquidFloor) {
		liquidFloorAnimateShader.begin();
		liquidFloorAnimateShader.setUniformf(
			uWaveState, waveState(0.5f)
		);
		liquidFloorAnimateShader.end();
	} else {
		batch.setShader(GameScreen.defaultShader);
	}
	batch.setProjectionMatrix(viewport.getCamera().combined);
	batch.begin();
	drawFloors(false);
	if (animateLiquidFloor) {
		batch.setShader(liquidFloorAnimateShader);
		drawFloors(true);
		batch.setShader(GameScreen.defaultShader);
	}
	drawTransitions(false);
	if (animateLiquidFloor) {
		batch.setShader(liquidFloorAnimateShader);
		drawTransitions(true);
	}
	batch.end();
	if (animateLiquidFloor) {
		batch.setShader(GameScreen.defaultShader);
	}
}

private float waveState(float frequency) {
	return (float) ((Math.PI * 2 / 2000 * frequency) * System.currentTimeMillis() % (Math.PI * 2));
}

private void drawFloors(boolean liquid) {
	int maxX = gameScreen.getMaxRenderCellX();
	int maxY = gameScreen.getMaxRenderCellY();
	for (int x = viewport.getStartCellX(); x < maxX; x++) {
		for (int y = viewport.getStartCellY(); y < maxY; y++) {
			RenderCell cell = gameScreen.renderPlane.getCell(x, y);
			if (cell != null && (cell.getFloor().isLiquid() == liquid || !animateLiquidFloor)) {
				drawFloor(cell.getFloor(), x, y);
			}
		}
	}
}

void drawFloor(FloorType floorType, int x, int y) {
	if (!gameScreen.isFloorUnderWallShouldBeDrawn(x, y)) {
		// Don't draw floor on cells that are right under a wall on the edge of field of view,
		// because drawing one produces an unpleasant and unnatural effect.
		return;
	}
	TextureRegion floor = getFloorTextureByCell(floorType, x, y);
	batch.draw(floor, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
}

private void drawTransitions(boolean liquid) {
	// Draw transitions
	for (int x = viewport.getStartCellX(); x < gameScreen.getMaxRenderCellX(); x++) {
		for (int y = viewport.getStartCellY(); y < gameScreen.getMaxRenderCellY(); y++) {
			RenderCell cell = gameScreen.renderPlane.getCell(x, y);
			// (!A || B) — see "Logical implication" in Wikipedia.
			// Shortly, if there is a wall, then floor under it should need to be drawn for a condition to pass.
			if (cell != null
				&& (!cell.hasWall() || gameScreen.isFloorUnderWallShouldBeDrawn(x, y))
				) {
				drawFloorTransitionsInCell(cell, liquid);
			}
		}
	}
}

private TextureRegion getFloorTextureByCell(FloorType floor, int x, int y) {
	int key = (int) (Math.round(Math.abs(Math.sin(x * y)) * floorIndices.get(floor)));
	return floorRegions.get(floor).get(key);
}

private void cacheRegions() {
	Map<String, FloorType> name2Type = new HashMap<>();
	Map<String, Array<TextureAtlas.AtlasRegion>> name2Regions = new HashMap<>();
	for (FloorType floorType : Registry.floorTypes) {
		name2Type.put(floorType.getResourceName(), floorType);
		floorRegions.put(floorType, new HashMap<Integer, TextureRegion>());
		assert atlasFloors.findRegion(floorType.getResourceName()) != null;
	}
	for (TextureAtlas.AtlasRegion region : atlasFloors.getRegions()) {
		if (name2Type.containsKey(region.name)) {
			floorRegions.get(name2Type.get(region.name)).put(region.index, region);
		} else {
			Tendiwa.getLogger().warn("Floor with name " + region.name + "_" + region.index + " has its sprite, but it is not declared in ontology");
		}
	}
	for (Map.Entry<String, FloorType> e : name2Type.entrySet()) {
		Array<TextureAtlas.AtlasRegion> regions = atlasFloors.findRegions(e.getKey());
		name2Regions.put(e.getKey(), regions);
		floorIndices.put(e.getValue(), regions.size - 1);
	}
	// Validate that all images have indices from 0 to n without skips.
	for (Map.Entry<String, Array<TextureAtlas.AtlasRegion>> e : name2Regions.entrySet()) {
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

private TransitionsToFloor getFloorTransitionsProvider(FloorType floorType) {
	return floorTransitionsProviders.get(floorType);
}

void drawFloorTransitionsInCell(RenderCell cell, boolean liquid) {
	FloorType self = cell.getFloor();
	RenderCell renderCell = gameScreen.renderPlane.getCell(cell.getX(), cell.getY() + 1);
	// Indices 0 and 2 are swapped
	floorsFrom4Sides[2] = cell.getY() + 1 < world.getHeight() && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.renderPlane.getCell(cell.getX() + 1, cell.getY());
	floorsFrom4Sides[1] = cell.getX() + 1 < world.getWidth() && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.renderPlane.getCell(cell.getX(), cell.getY() - 1);
	floorsFrom4Sides[0] = cell.getY() > 0 && renderCell != null ? renderCell.getFloor() : self;
	renderCell = gameScreen.renderPlane.getCell(cell.getX() - 1, cell.getY());
	floorsFrom4Sides[3] = cell.getX() > 0 && renderCell != null ? renderCell.getFloor() : self;
	if (floorsFrom4Sides[0] != self || floorsFrom4Sides[1] != self || floorsFrom4Sides[2] != self || floorsFrom4Sides[3] != self) {
		drawCellWithTransitions(cell.getX(), cell.getY(), self, liquid);
	}
}

private void drawCellWithTransitions(int x, int y, FloorType self, boolean liquid) {
	// Get individual transition pixmap for each side
	for (CardinalDirection dir : CardinalDirection.values()) {
		int[] d = dir.side2d();
		int i = dir.getCardinalIndex();
		if (floorsFrom4Sides[i] != self
			&& (!animateLiquidFloor || gameScreen.renderPlane.getCell(x + d[0], y + d[1]).getFloor().isLiquid() == liquid)
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
