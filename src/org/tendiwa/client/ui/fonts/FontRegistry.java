package org.tendiwa.client.ui.fonts;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class FontRegistry {

private final FreeTypeFontGenerator generator;
private Map<Integer, Map<Boolean, Map<String, BitmapFont>>> fonts = new HashMap<>();
private String defaultCharacters = "";

@Inject
public FontRegistry(FreeTypeFontGenerator generator) {
	this.generator = generator;
}

public BitmapFont obtain(int size, boolean flipped) {
	if (!fonts.containsKey(size)) {
		fonts.put(size, new HashMap<Boolean, Map<String, BitmapFont>>());
	}
	if (!fonts.get(size).containsKey(flipped)) {
		fonts.get(size).put(flipped, new HashMap<String, BitmapFont>());
	}
	BitmapFont bitmapFont = fonts.get(size).get(flipped).get(defaultCharacters);
	if (bitmapFont == null) {
		bitmapFont = generator.generateFont(size, defaultCharacters, flipped);
		fonts.get(size).get(flipped).put(defaultCharacters, bitmapFont);
	}
	assert bitmapFont != null;
	return bitmapFont;
}

private void addDefaultCharacters(String characters) {
	defaultCharacters += characters;
}

}
