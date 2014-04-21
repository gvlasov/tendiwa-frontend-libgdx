package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasWalls extends TextureAtlas {
private static AtlasWalls INSTANCE = new AtlasWalls();

private AtlasWalls() {
	super(Gdx.files.internal("pack/walls.atlas"), true);
}

public static AtlasWalls getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasWalls()) : INSTANCE;
}
}
