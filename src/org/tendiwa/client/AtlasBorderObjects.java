package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasBorderObjects extends TextureAtlas {
private static AtlasBorderObjects INSTANCE = new AtlasBorderObjects();

private AtlasBorderObjects() {
	super(Gdx.files.internal("pack/borderObjects.atlas"), true);
}

public static AtlasBorderObjects getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasBorderObjects()) : INSTANCE;
}
}
