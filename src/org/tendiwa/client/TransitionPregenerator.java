package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import tendiwa.core.CardinalDirection;

/**
 * Creates and returns images of something gradually transitioning to something else. Once an instance of
 * TransitionPregenerator is created, all transition images are generated. After that you can fetch from here images
 * corresponding to a particular transition.
 *
 * @see TileTextureRegionProvider For where the transitions are drawn and how are they managed.
 */
public abstract class TransitionPregenerator {
final static int TILE_SIZE = GameScreen.TILE_SIZE;
private static TileTextureRegionProvider regionProvider;
private final TextureRegion[] textures;
private final int variationsPerTransition;
private final int totalTransitionTextures;

public TransitionPregenerator(int variationsPerTransition) {
	this.variationsPerTransition = variationsPerTransition;
	totalTransitionTextures = variationsPerTransition * 4;
	textures = new TextureRegion[totalTransitionTextures];
}

static void initTileTextureRegionProvider(int numberOfPlaces) {
	if (regionProvider == null) {
		regionProvider = new TileTextureRegionProvider(numberOfPlaces, TILE_SIZE, TILE_SIZE);
	}
}

protected void createTransitions() {
	regionProvider.getFbo().begin();
	regionProvider.getTextureDrawingBatch().begin();
	for (CardinalDirection dir : CardinalDirection.values()) {
		createTransitionVariants(dir);
	}
	regionProvider.getTextureDrawingBatch().end();
	regionProvider.getFbo().end();
}

/**
 * Fills a TextureRegion with up to 4 transitions from cardinal sides.
 */
private void createTransitionVariants(CardinalDirection dir) {
	// Designate a new region of texture in framebuffer
	int end = (dir.getCardinalIndex() + 1) * variationsPerTransition;
	for (int i = dir.getCardinalIndex() * variationsPerTransition; i < end; i++) {
		textures[i] = regionProvider.obtainFboTextureRegion();
		regionProvider.getFboTexture().draw(
			createTransition(dir),
			textures[i].getRegionX(),
			textures[i].getRegionY()
		);
	}
}

/**
 * Creates a new transition Pixmap to a specified side.
 *
 * @param direction
 * 	To which side to transit.
 * @return A new Pixmap with an image of transition to that side.
 */
public abstract Pixmap createTransition(CardinalDirection direction);

public void drawTransitions(SpriteBatch batch, int x, int y, boolean[] neighborSelected, int screenX, int screenY) {
	assert neighborSelected.length == 4;
	for (int i = 0; i < 4; i++) {
		if (neighborSelected[i]) {
			batch.draw(
				getTransition(i, screenX, screenY),
				x, y
			);
		}
	}
}

TextureRegion getTransition(int cardinalIndex, int screenX, int screenY) {
	// Swap vertical directions
	if (cardinalIndex == 0) {
		cardinalIndex = 2;
	} else if (cardinalIndex == 2) {
		cardinalIndex = 0;
	}
	int hashShift = (screenY % variationsPerTransition + screenX) % variationsPerTransition;
	int index = cardinalIndex * variationsPerTransition + hashShift;
	return textures[index];
}

public TextureRegion getTransition(CardinalDirection dir, int screenX, int screenY) {
	return getTransition(dir.getCardinalIndex(), screenX, screenY);
}
}