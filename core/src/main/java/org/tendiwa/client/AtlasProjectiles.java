package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasProjectiles extends TextureAtlas {
private static AtlasProjectiles INSTANCE = new AtlasProjectiles();

private AtlasProjectiles() {
	super(Gdx.files.internal("pack/projectiles.atlas"), true);
}

public static AtlasProjectiles getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasProjectiles()) : INSTANCE;
}
}
