package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;
import org.tendiwa.client.ui.factories.FloorTransitionsProvidersRegistry;
import org.tendiwa.core.*;
import org.tendiwa.core.clients.RenderCell;
import org.tendiwa.core.clients.RenderWorld;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class FloorLayer {
private final World world;
private final RenderWorld renderWorld;
private final GameScreenViewport viewport;
private final Logger logger;
private final ShaderProgram defaultShader;
private final FloorTransitionsProvidersRegistry registry;
	private final Encyclopedia encyclopedia;
	private final GraphicsConfig config;
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

@Inject
public FloorLayer(
	@Named("current_player_world") World world,
	RenderWorld renderWorld,
	GameScreenViewport viewport,
	Logger logger,
	@Named("shader_liquid_floor_animate") ShaderProgram liquidFloorAnimateShader,
	@Named("shader_default") ShaderProgram defaultShader,
	FloorTransitionsProvidersRegistry registry,
	Encyclopedia encyclopedia,
	GraphicsConfig config
) {
	this.world = world;
	this.renderWorld = renderWorld;
	this.viewport = viewport;
	this.logger = logger;
	this.defaultShader = defaultShader;
	this.registry = registry;
	this.encyclopedia = encyclopedia;
	this.config = config;
	atlasFloors = new TextureAtlas(Gdx.files.internal("pack/floors.atlas"), true);
	cacheRegions();
	batch = new SpriteBatch();
	transitionsFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getWindowWidthPixels(), viewport.getWindowHeightPixels(), false);
	this.liquidFloorAnimateShader = liquidFloorAnimateShader;
	uWaveState = liquidFloorAnimateShader.getUniformLocation("waveState");
}

void draw() {
	// Config is read once per frame
	animateLiquidFloor = config.animateLiquidFloor;
	if (animateLiquidFloor) {
		liquidFloorAnimateShader.begin();
		liquidFloorAnimateShader.setUniformf(
			uWaveState, waveState(0.5f)
		);
		liquidFloorAnimateShader.end();
	} else {
		batch.setShader(defaultShader);
	}
	batch.setProjectionMatrix(viewport.getCamera().combined);
	batch.begin();
	drawFloors(false);
	if (animateLiquidFloor) {
		batch.setShader(liquidFloorAnimateShader);
		drawFloors(true);
		batch.setShader(defaultShader);
	}
	drawTransitions(false);
	if (animateLiquidFloor) {
		batch.setShader(liquidFloorAnimateShader);
		drawTransitions(true);
	}
	batch.end();
	if (animateLiquidFloor) {
		batch.setShader(defaultShader);
	}
}

private float waveState(float frequency) {
	return (float) ((Math.PI * 2 / 2000 * frequency) * System.currentTimeMillis() % (Math.PI * 2));
}

private void drawFloors(boolean liquid) {
	int maxX = viewport.getMaxRenderCellX();
	int maxY = viewport.getMaxRenderCellY();
	for (int x = viewport.getStartCellX(); x < maxX; x++) {
		for (int y = viewport.getStartCellY(); y < maxY; y++) {
			RenderCell cell = renderWorld.getCurrentPlane().getCell(x, y);
			if (cell != null && (cell.getFloor().isLiquid() == liquid || !animateLiquidFloor)) {
				drawFloor(cell.getFloor(), x, y);
			}
		}
	}
}

void drawFloor(FloorType floorType, int x, int y) {
	if (!isFloorUnderWallShouldBeDrawn(x, y)) {
		// Don't draw floor on cells that are right under a wall on the edge of field of view,
		// because drawing one produces an unpleasant and unnatural effect.
		return;
	}
	TextureRegion floor = getFloorTextureByCell(floorType, x, y);
	batch.draw(floor, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
}

/**
 * Checks if a floor tile should be drawn under certain cell.
 *
 * @param x
 * 	World x coordinate of a cell.
 * @param y
 * 	World y coordinate of a cell.
 * @return False if there is a wall in cell {x:y} and cell {x:y+1} is not yet seenCells, true otherwise.
 */
boolean isFloorUnderWallShouldBeDrawn(int x, int y) {
	return !(renderWorld.getCurrentPlane().getCell(x, y).hasWall() && !renderWorld.getCurrentPlane().hasCell(x, y + 1));
}

private void drawTransitions(boolean liquid) {
	// Draw transitions
	for (int x = viewport.getStartCellX(); x < viewport.getMaxRenderCellX(); x++) {
		for (int y = viewport.getStartCellY(); y < viewport.getMaxRenderCellY(); y++) {
			RenderCell cell = renderWorld.getCurrentPlane().getCell(x, y);
			// (!A || B) — see "Logical implication" in Wikipedia.
			// Shortly, if there is a wall, then floor under it should need to be drawn for a condition to pass.
			if (cell != null
				&& (!cell.hasWall() || isFloorUnderWallShouldBeDrawn(x, y))
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
	for (FloorType floorType : encyclopedia.floors()) {
		name2Type.put(floorType.getResourceName(), floorType);
		floorRegions.put(floorType, new HashMap<Integer, TextureRegion>());
		assert atlasFloors.findRegion(floorType.getResourceName()) != null;
	}
	for (TextureAtlas.AtlasRegion region : atlasFloors.getRegions()) {
		if (name2Type.containsKey(region.name)) {
			floorRegions.get(name2Type.get(region.name)).put(region.index, region);
		} else {
			logger.warn("Floor with name " + region.name + "_" + region.index + " has its sprite, but it is not declared in ontology");
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

void drawFloorTransitionsInCell(RenderCell cell, boolean liquid) {
	FloorType self = cell.getFloor();
	RenderCell renderCell = renderWorld.getCurrentPlane().getCell(cell.getX(), cell.getY() + 1);
	// Indices 0 and 2 are swapped
	floorsFrom4Sides[2] = cell.getY() + 1 < world.getHeight() && renderCell != null ? renderCell.getFloor() : self;
	renderCell = renderWorld.getCurrentPlane().getCell(cell.getX() + 1, cell.getY());
	floorsFrom4Sides[1] = cell.getX() + 1 < world.getWidth() && renderCell != null ? renderCell.getFloor() : self;
	renderCell = renderWorld.getCurrentPlane().getCell(cell.getX(), cell.getY() - 1);
	floorsFrom4Sides[0] = cell.getY() > 0 && renderCell != null ? renderCell.getFloor() : self;
	renderCell = renderWorld.getCurrentPlane().getCell(cell.getX() - 1, cell.getY());
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
			&& (!animateLiquidFloor || renderWorld.getCurrentPlane().getCell(x + d[0], y + d[1]).getFloor().isLiquid() == liquid)
			) {
			TransitionsToFloor floorTransitionsProvider = registry.obtain(floorsFrom4Sides[i]);
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
