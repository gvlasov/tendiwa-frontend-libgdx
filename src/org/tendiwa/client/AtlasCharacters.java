package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasCharacters extends TextureAtlas {
private static AtlasCharacters INSTANCE = new AtlasCharacters();

private AtlasCharacters() {
	super(Gdx.files.internal("pack/characters.atlas"), true);
}

public static AtlasCharacters getInstance() {
	return INSTANCE == null ? (INSTANCE = new AtlasCharacters()) : INSTANCE;
}
}
