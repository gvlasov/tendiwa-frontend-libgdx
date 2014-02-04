package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.google.inject.Provider;

public class FreeTypeFontGeneratorProvider implements Provider<FreeTypeFontGenerator> {
@Override
public FreeTypeFontGenerator get() {
	return new FreeTypeFontGenerator(Gdx.files.internal("default.fnt"));
}
}
