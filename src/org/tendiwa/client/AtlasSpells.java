package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasSpells extends TextureAtlas {
private static AtlasSpells INSTANCE = new AtlasSpells();

private AtlasSpells() {
	super(Gdx.files.internal("pack/spells.atlas"), false);
}

public static AtlasSpells getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasSpells()) : INSTANCE;
}
}
