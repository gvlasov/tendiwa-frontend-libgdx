package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Character;
import tendiwa.core.CharacterAspect;
import tendiwa.core.Item;
import tendiwa.core.Items;

public class CharacterActor extends Actor {
private static final TextureAtlas atlasCharacters = AtlasCharacters.getInstance();
private static final TextureAtlas atlasBodies = new TextureAtlas(Gdx.files.internal("pack/bodies.atlas"), true);
private static final TextureAtlas atlasApparel = new TextureAtlas(Gdx.files.internal("pack/apparel.atlas"), true);
private static final TextureAtlas atlasWielded = new TextureAtlas(Gdx.files.internal("pack/wielded.atlas"), true);
private static final SpriteBatch batch = new OrthoBatch();
private final Character character;
private final TextureRegion texture;
private FrameBuffer frameBuffer;

public CharacterActor(Character character) {
	this.character = character;
	setX(character.getX());
	setY(character.getY());
	if (character.getType().hasAspect(CharacterAspect.HUMANOID)) {
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, true);
		updateTexture();
		texture = new TextureRegion(frameBuffer.getColorBufferTexture());
	} else {
		String typeName = character.getType().getResourceName();
		texture = atlasCharacters.findRegion(typeName);
	}
}

/**
 * Updates texture of those characters that are constructed from several images: body, bodily features, apparel,
 * weapons.
 */
public void updateTexture() {
	frameBuffer.begin();
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	batch.begin();
	String resourceName = character.getType().getResourceName();
	TextureAtlas.AtlasRegion region = atlasBodies.findRegion(resourceName);
	if (region == null) {
		throw new RuntimeException("No image for character type " + character.getType().getResourceName());
	}
	batch.draw(region, 0, 0, 32, 32);
	for (Item item : character.getEquipment()) {
		if (Items.isWearable(item.getType())) {
			batch.draw(atlasApparel.findRegion(item.getType().getResourceName()), 0, 0);
		} else if (Items.isWieldable(item.getType())) {
			batch.draw(atlasWielded.findRegion(item.getType().getResourceName()), 0, 0);
		}
	}
	batch.end();
	frameBuffer.end();
}

@Override
public void draw(Batch batch, float parentAlpha) {
	batch.draw(
		texture,
		(int) (getX() * GameScreen.TILE_SIZE),
		(int) (getY() * GameScreen.TILE_SIZE),
		getOriginX() * GameScreen.TILE_SIZE,
		getOriginY() * GameScreen.TILE_SIZE,
		GameScreen.TILE_SIZE,
		GameScreen.TILE_SIZE,
		getScaleX(),
		getScaleY(),
		getRotation()
	);
}

private static class OrthoBatch extends SpriteBatch {
	private OrthoBatch() {
		OrthographicCamera camera = new OrthographicCamera(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
		camera.setToOrtho(true, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
		setProjectionMatrix(camera.combined);
	}
}
}
