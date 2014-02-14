package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class GdxGraphicsProvider implements Provider<Graphics> {
@Override
public Graphics get() {
	assert Gdx.graphics != null;
	return Gdx.graphics;
}
}
