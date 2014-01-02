package org.tendiwa.client;

import tendiwa.core.Tendiwa;

public class DesktopStarter {
public static void main(String[] args) {
	if (args.length > 0 && args[0].equals("atlas")) {
		buildResources();
	} else {
		loadGame();
	}
}

public static void loadGame() {
	TendiwaGame.getInstance().startup();
	Tendiwa.getLogger().setLevel(org.apache.log4j.Level.DEBUG);
}

public static void buildResources() {
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/floors", "floors");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/walls", "walls", false);
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/objects", "objects");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/characters", "characters");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/items", "items");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/ui", "ui");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll/bodies", "bodies");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll/apparel", "apparel");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll/wielded", "wielded");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/ranged", "projectiles");
	ResourcesBuilder.buildTexturesToAtlas("/home/suseika/Projects/tendiwa/MainModule/data/images/spells", "spells");
}
}