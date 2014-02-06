package org.tendiwa.client.ui.factories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.WallImageCache;
import org.tendiwa.core.WallType;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class WallImageCacheRegistry {
private static final int NUMBER_OF_SLOTS_IN_CACHE = 144;
public final Map<WallType, WallImageCache> caches = new HashMap<>();
private final WallImageCacheFactory factory;

@Inject
WallImageCacheRegistry(WallImageCacheFactory factory) {
	this.factory = factory;
}

public WallImageCache obtain(WallType type) {
	WallImageCache wallImageCache = caches.get(type);
	if (wallImageCache == null) {
		wallImageCache = factory.create(type, NUMBER_OF_SLOTS_IN_CACHE);
		caches.put(type, wallImageCache);
	}
	return wallImageCache;
}
}
