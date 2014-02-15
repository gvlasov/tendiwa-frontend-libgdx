package org.tendiwa.client.extensions.std;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultSkin extends Skin {

@Inject
private DefaultSkin() {
	super(Gdx.files.internal("assets/uiskin.json"), new TextureAtlas(Gdx.files.internal("assets/uiskin.atlas")));
}
}
