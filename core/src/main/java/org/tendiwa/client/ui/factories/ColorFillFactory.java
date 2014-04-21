package org.tendiwa.client.ui.factories;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ColorFillFactory {

private final Skin skin;

ColorFillFactory() {
	skin = new Skin();
	Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
	pixmap.setColor(1, 1, 1, 1);
	pixmap.fill();
	skin.add("white", new Texture(pixmap));
}

public Image create(Color color) {
	// Creates and returns a tinted copy of white color
	return new Image(skin.newDrawable("white", color));
}
}
