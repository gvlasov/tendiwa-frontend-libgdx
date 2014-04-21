package org.tendiwa.client;

import com.google.inject.Provider;

public class TransitionsTileTextureRegionProvider implements Provider<TileTextureRegionProvider> {

@Override
public TileTextureRegionProvider get() {
	return new TileTextureRegionProvider(100, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
}
}
