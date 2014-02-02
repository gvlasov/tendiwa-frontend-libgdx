package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasUi extends TextureAtlas {
AtlasUi() {
	super(Gdx.files.internal("pack/ui.atlas"), true);
}
}
