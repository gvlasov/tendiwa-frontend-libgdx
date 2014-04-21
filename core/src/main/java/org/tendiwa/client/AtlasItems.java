package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasItems extends TextureAtlas {
private static AtlasItems INSTANCE = new AtlasItems();

private AtlasItems() {
	super(Gdx.files.internal("pack/items.atlas"), true);
}

public static AtlasItems getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasItems()) : INSTANCE;
}
}
