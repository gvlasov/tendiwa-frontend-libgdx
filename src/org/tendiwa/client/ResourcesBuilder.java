package org.tendiwa.client;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class ResourcesBuilder {
static void buildTexturesToAtlas(String directory, String atlasName) {
	TexturePacker2.Settings settings = new TexturePacker2.Settings();
	settings.flattenPaths = true;
	TexturePacker2.process(directory, "pack", atlasName);
}
}
