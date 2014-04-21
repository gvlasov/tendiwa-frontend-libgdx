package org.tendiwa.client;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class ResourcesBuilder {
void buildTexturesToAtlas(String directory, String atlasName, boolean combineSubdirectories) {
	TexturePacker.Settings settings = new TexturePacker.Settings();
	settings.flattenPaths = true;
	settings.combineSubdirectories = combineSubdirectories;
	settings.maxHeight = 100000;
	settings.maxHeight = 100000;
	settings.pot = false;
	TexturePacker.process(settings, directory, "pack", atlasName);
}

void buildTexturesToAtlas(String directory, String atlasName) {
	buildTexturesToAtlas(directory, atlasName, true);
}
public void buildResources() {
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/floors", "floors");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/walls", "walls", false);
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/objects", "objects");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/characters", "characters");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/items", "items");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/ui", "ui");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/chardoll/bodies", "bodies");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/chardoll/apparel", "apparel");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/chardoll/wielded", "wielded");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/ranged", "projectiles");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/spells", "spells");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/src/main/resources/images/borderObjects", "borderObjects");
}
}
