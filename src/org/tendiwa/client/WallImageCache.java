package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.tendiwa.core.WallType;

/**
 * Caches images of {@link WallActor}s, which are composed of wall's image and various shading and darkening with
 * transitions from fully bright part to a shaded or darkened one of a neighbor wall. Drawing a wall from scratch is
 * quite expensive, hence this class.
 */
public class WallImageCache {
public static final int SIDE_N = 1 << 0;
public static final int SIDE_E = 1 << 1;
public static final int SIDE_S = 1 << 2;
public static final int SIDE_W = 1 << 3;
public static final int SOUTH_WALL_SHADE = 1 << 4;
public static final int SOUTH_WALL_DARK = 1 << 5;
public static final int SHADE_N = 1 << 6;
public static final int SHADE_E = 1 << 7;
public static final int SHADE_S = 1 << 8;
public static final int SHADE_W = 1 << 9;
public static final int DARK_N = 1 << 10;
public static final int DARK_E = 1 << 11;
public static final int DARK_S = 1 << 12;
public static final int DARK_W = 1 << 13;
public static final int SHADE_SOUTH_WALL_LEFT = 1 << 14;
public static final int SHADE_SOUTH_WALL_RIGHT = 1 << 15;
public static final int DARK_SOUTH_WALL_LEFT = 1 << 16;
public static final int DARK_SOUTH_WALL_RIGHT = 1 << 17;
public static final int VISIBLE = 1 << 18;
private final TileTextureRegionProvider regionProvider;
private final BiMap<Integer, Integer> hash2index = HashBiMap.create();
private final TextureRegion[] regions;
private int lastHashOccupied;
private int slots;

/**
 * @param type
 * 	Type of a wall whose images are cached.
 * @param slots
 * 	How many wall images may be cached.
 */
WallImageCache(WallType type, int slots) {
	this.slots = slots;
	regions = new TextureRegion[slots];
	TextureAtlas.AtlasRegion region = AtlasWalls.getInstance().findRegion(type.getResourceName());
	assert region != null : type.getResourceName();
	regionProvider = new TileTextureRegionProvider(slots, region.getRegionWidth(), region.getRegionHeight());
	for (int i = 0; i < slots; i++) {
		regions[i] = regionProvider.obtainFboTextureRegion();
	}
	lastHashOccupied = 0;
	regionProvider.getFbo().begin();
	Gdx.gl.glClearColor(0, 0, 0, 1);
	regionProvider.getFbo().end();
}

/**
 * Return the object that holds a framebuffer and TextureRegions where images of this WallType are drawn.
 *
 * @return
 */
public TileTextureRegionProvider getRegionProvider() {
	return regionProvider;
}

/**
 * Returns the cached image for a wall described by {@code imageHash}.
 *
 * @param imageHash
 * 	Description of a wall image composed of a sum of this class's constant fields.
 * @return Cached image for a wall described by {@code imageHash}.
 */
public TextureRegion getImage(int imageHash) {
	TextureRegion region = regions[hash2index.get(imageHash)];
	return region;
}

/**
 * Draws a {@code texture} to {@link WallImageCache#regionProvider} to be later available through {@link
 * WallImageCache#getImage(int)} under hash {@code imageHash}.
 *
 * @param imageHash
 * 	Description of a wall image composed of a sum of this class's constant fields. // * @param texture // * 	A texture
 * 	to draw to {@link WallImageCache#regionProvider}'s framebuffer be later available under given hash.
 */
public void putImage(int imageHash, FrameBuffer fb) {
	TextureRegion regionForIndex = regions[occupyNextIndexInCache(imageHash)];
	fb.begin();
	Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fb.getWidth(), fb.getHeight());
	Pixmap croppedPixmap = new Pixmap(regionForIndex.getRegionWidth(), regionForIndex.getRegionHeight(), Pixmap.Format.RGBA8888);
	fb.end();
	croppedPixmap.drawPixmap(pixmap, 0, 0, 0, WallActor.maxHeight - regionForIndex.getRegionHeight(), regionForIndex.getRegionWidth(), regionForIndex.getRegionHeight());
	regionProvider.getFboTexture().draw(croppedPixmap, regionForIndex.getRegionX(), regionForIndex.getRegionY());
}

/**
 * Maps image hash to an index from 0 to {@link WallImageCache#slots}. Each successive method call will increase index
 * by 1, and when it exceeds {@link WallImageCache#slots}, then index is set back to 0 to reuse previously used slots.
 *
 * @param imageHash
 * 	Description of a wall image composed of a sum of this class's constant fields.
 */
private int occupyNextIndexInCache(int imageHash) {
	int assignedIndex = lastHashOccupied;
	hash2index.inverse().remove(lastHashOccupied);
	hash2index.put(imageHash, lastHashOccupied);
	lastHashOccupied++;
	if (lastHashOccupied == slots) {
		lastHashOccupied = 0;
	}
	return assignedIndex;
}

/**
 * Checks if an image has already been put to this WallImageCache under given {@code imageHash}.
 *
 * @param imageHash
 * 	Description of a wall image composed of a sum of this class's constant fields.
 * @return true if there is such image, false otherwise.
 */
public boolean hasImage(int imageHash) {
	return hash2index.containsKey(imageHash);
}
}
