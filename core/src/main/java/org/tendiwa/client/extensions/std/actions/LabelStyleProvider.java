package org.tendiwa.client.extensions.std.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.tendiwa.client.ui.fonts.FontRegistry;

@Singleton
public class LabelStyleProvider implements Provider<Label.LabelStyle> {
private final FontRegistry fontRegistry;

@Inject
LabelStyleProvider(
	FontRegistry fontRegistry
) {
	this.fontRegistry = fontRegistry;
}

@Override
public Label.LabelStyle get() {
	return new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
}
}
