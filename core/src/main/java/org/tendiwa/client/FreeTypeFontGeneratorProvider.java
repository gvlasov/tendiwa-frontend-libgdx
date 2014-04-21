package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class FreeTypeFontGeneratorProvider implements Provider<FreeTypeFontGenerator> {
@Override
public FreeTypeFontGenerator get() {
	return new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf"));
}
}
