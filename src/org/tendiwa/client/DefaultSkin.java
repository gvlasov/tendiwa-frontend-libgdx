package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DefaultSkin extends Skin {
public static DefaultSkin INSTANCE;

private DefaultSkin() {
	super(Gdx.files.internal("assets/uiskin.json"), new TextureAtlas(Gdx.files.internal("assets/uiskin.atlas")));
}

public static DefaultSkin getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new DefaultSkin();
	}
	return INSTANCE;
}
}
