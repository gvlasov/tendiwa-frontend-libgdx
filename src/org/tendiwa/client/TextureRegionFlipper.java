package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionFlipper {
public static TextureRegion flip(TextureRegion region) {
	TextureRegion newRegion = new TextureRegion(region);
	newRegion.flip(false, true);
	return newRegion;
}
}
