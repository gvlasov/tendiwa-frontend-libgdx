package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.*;

@Singleton
public class ItemsLayer {
private final Batch batch;
private final GameScreenViewport viewport;
private final RenderWorld renderWorld;
private final Character player;
private final TextureRegion multipleItemsMarker;

@Inject
ItemsLayer(@Named("game_screen_batch") Batch batch, GameScreenViewport viewport, RenderWorld renderWorld, AtlasUi atlasUi, Character player) {
	this.batch = batch;
	this.viewport = viewport;
	this.renderWorld = renderWorld;
	this.player = player;
	multipleItemsMarker = atlasUi.findRegion("multiItem");
}

void draw() {
	HorizontalPlane plane = player.getPlane();
	RenderPlane renderPlane = renderWorld.getCurrentPlane();
	int maxX = viewport.getMaxRenderCellX();
	int maxY = viewport.getMaxRenderCellY();
	batch.begin();
	for (int x = viewport.getStartCellX(); x < maxX; x++) {
		for (int y = viewport.getStartCellY(); y < maxY; y++) {
			if (renderPlane.isCellVisible(x, y) && plane.hasAnyItems(x, y)) {
				// Check for objective view (we could maintain character's own subjective view on items,
				// but that's difficult and maybe will appear later.
				ItemCollection items = plane.getItems(x, y);
				assert items.iterator().hasNext() : items.size();
				Item item = items.iterator().next();
				assert item != null;
				batch.draw(
					AtlasItems.getInstance().findRegion(item.getType().getResourceName()),
					x * GameScreen.TILE_SIZE,
					y * GameScreen.TILE_SIZE
				);
				if (items.size() > 1) {
					batch.draw(multipleItemsMarker, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
				}
			}
		}
	}
	batch.setShader(GameScreen.drawWithRGB06Shader);
	for (int x = viewport.getStartCellX(); x < maxX; x++) {
		for (int y = viewport.getStartCellY(); y < maxY; y++) {
			if (renderPlane.hasAnyUnseenItems(x, y)) {
				for (RememberedItem item : renderPlane.getUnseenItems(x, y)) {
					batch.draw(
						getTexture(item.getType()),
						x * GameScreen.TILE_SIZE,
						y * GameScreen.TILE_SIZE
					);
				}
			}
		}
	}
	batch.end();
	batch.setShader(GameScreen.defaultShader);
}

private TextureAtlas.AtlasRegion getTexture(ItemType type) {
	return AtlasItems.getInstance().findRegion(type.getResourceName());
}

}
