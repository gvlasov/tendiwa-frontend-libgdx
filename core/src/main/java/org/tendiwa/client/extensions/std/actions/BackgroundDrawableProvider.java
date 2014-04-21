package org.tendiwa.client.extensions.std.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.tendiwa.client.ui.factories.ColorFillFactory;

@Singleton
public class BackgroundDrawableProvider implements Provider<Drawable> {
private final ColorFillFactory factory;

@Inject
BackgroundDrawableProvider(
	ColorFillFactory factory
) {

	this.factory = factory;
}

@Override
public Drawable get() {
	return factory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable();
}
}
