package org.tendiwa.client.ui.factories;

import org.tendiwa.client.WallImageCache;
import org.tendiwa.core.WallType;

public interface WallImageCacheFactory {
public WallImageCache create(WallType type, int numberOfSlots);
}
