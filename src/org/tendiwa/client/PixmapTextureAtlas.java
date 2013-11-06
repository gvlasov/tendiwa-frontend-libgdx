package org.tendiwa.client;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class PixmapTextureAtlas implements Disposable {

boolean shouldDispose = false;
private TextureAtlas textureAtlas;
private Pixmap textureAtlasPixmap;

public PixmapTextureAtlas(FileHandle textureAtlasImageFile, FileHandle textureAtlasFile) {
	this(new TextureAtlas(textureAtlasFile), new Pixmap(textureAtlasImageFile));
	this.shouldDispose = true;
}

public PixmapTextureAtlas(TextureAtlas textureAtlas, Pixmap textureAtlasPixmap) {
	this.textureAtlas = textureAtlas;
	this.textureAtlasPixmap = textureAtlasPixmap;
}

public Pixmap createPixmap(String regionName) {
	TextureAtlas.AtlasRegion region = textureAtlas.findRegion(regionName);

	System.out.println(regionName);
	int width = MathUtils.nextPowerOfTwo(region.getRegionWidth());
	int height = MathUtils.nextPowerOfTwo(region.getRegionHeight());

	Pixmap regionPixmap = new Pixmap(width, height, textureAtlasPixmap.getFormat());

	int x = (width / 2) - (region.getRegionWidth() / 2);
	int y = (height / 2) - (region.getRegionHeight() / 2);

	regionPixmap.drawPixmap(textureAtlasPixmap, x, y, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

	return regionPixmap;
}

@Override
public void dispose() {
	if (shouldDispose) {
		textureAtlas.dispose();
		textureAtlasPixmap.dispose();
	}
}

}
