package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import tendiwa.core.*;

public class ItemsLayer {
private final GameScreen gameScreen;
private final RenderWorld renderWorld;
private final TextureRegion multipleItemsMarker;

ItemsLayer(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	this.renderWorld = GameScreen.getRenderWorld();

	multipleItemsMarker = gameScreen.getAtlasUi().findRegion("multiItem");
}

void draw() {
	HorizontalPlane plane = Tendiwa.getWorld().getDefaultPlane();
	int maxX = gameScreen.getMaxRenderCellX();
	int maxY = gameScreen.getMaxRenderCellY();
	gameScreen.batch.begin();
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			if (renderWorld.isCellVisible(x, y) && plane.hasAnyItems(x, y)) {
				// Check for objective view (we could maintain character's own subjective view on items,
				// but that's difficult and maybe will appear later.
				ItemCollection items = plane.getItems(x, y);
				assert items.iterator().hasNext() : items.size();
				Item item = items.iterator().next();
				assert item != null;
				gameScreen.batch.draw(
					AtlasItems.getInstance().findRegion(item.getType().getResourceName()),
					x * GameScreen.TILE_SIZE,
					y * GameScreen.TILE_SIZE
				);
				if (items.size() > 1) {
					gameScreen.batch.draw(multipleItemsMarker, x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE);
				}
			}
		}
	}
	gameScreen.batch.setShader(GameScreen.drawWithRGB06Shader);
	for (int x = gameScreen.startCellX; x < maxX; x++) {
		for (int y = gameScreen.startCellY; y < maxY; y++) {
			if (renderWorld.hasAnyUnseenItems(x, y)) {
				for (RememberedItem item : renderWorld.getUnseenItems(x, y)) {
					gameScreen.batch.draw(
						getTexture(item.getType()),
						x * GameScreen.TILE_SIZE,
						y * GameScreen.TILE_SIZE
					);
				}
			}
		}
	}
	gameScreen.batch.end();
	gameScreen.batch.setShader(GameScreen.defaultShader);
}

private TextureAtlas.AtlasRegion getTexture(ItemType type) {
	return AtlasItems.getInstance().findRegion(type.getResourceName());
}

}
