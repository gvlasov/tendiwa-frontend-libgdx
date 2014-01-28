package org.tendiwa.client;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class ResourcesBuilder {
void buildTexturesToAtlas(String directory, String atlasName, boolean combineSubdirectories) {
	TexturePacker2.Settings settings = new TexturePacker2.Settings();
	settings.flattenPaths = true;
	settings.combineSubdirectories = combineSubdirectories;
	settings.maxHeight = 100000;
	settings.maxHeight = 100000;
	settings.pot = false;
	TexturePacker2.process(settings, directory, "pack", atlasName);
}

void buildTexturesToAtlas(String directory, String atlasName) {
	buildTexturesToAtlas(directory, atlasName, true);
}
public void buildResources() {
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/floors", "floors");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/walls", "walls", false);
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/objects", "objects");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/characters", "characters");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/items", "items");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/ui", "ui");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll/bodies", "bodies");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll/apparel", "apparel");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll/wielded", "wielded");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/ranged", "projectiles");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/spells", "spells");
	buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/borderObjects", "borderObjects");
}
}
