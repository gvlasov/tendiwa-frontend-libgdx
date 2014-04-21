package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasObjects extends TextureAtlas {
private static AtlasObjects INSTANCE = new AtlasObjects();

private AtlasObjects() {
	super(Gdx.files.internal("pack/objects.atlas"), true);
}

public static AtlasObjects getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasObjects()) : INSTANCE;
}
}
