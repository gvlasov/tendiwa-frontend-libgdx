package org.tendiwa.client;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class ResourcesBuilder {
static void buildTexturesToAtlas(String directory, String atlasName) {
	TexturePacker2.Settings settings = new TexturePacker2.Settings();
	settings.flattenPaths = true;
	settings.combineSubdirectories = true;
	settings.maxHeight = 100000;
	settings.maxHeight = 100000;
	settings.pot = false;
	TexturePacker2.process(directory, "pack", atlasName);
}
}
