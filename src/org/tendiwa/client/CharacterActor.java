package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Character;
import tendiwa.core.Item;

public class CharacterActor extends Actor {
private static final TextureAtlas atlasCharacters = new TextureAtlas(Gdx.files.internal("pack/characters.atlas"), true);
private static final TextureAtlas atlasBodies = new TextureAtlas(Gdx.files.internal("pack/bodies.atlas"), true);
private static final TextureAtlas atlasApparel = new TextureAtlas(Gdx.files.internal("pack/apparel.atlas"), true);
private static final TextureAtlas atlasWielded = new TextureAtlas(Gdx.files.internal("pack/wielded.atlas"), true);
private static final SpriteBatch batch = new Batch();
private final Character character;
private final TextureRegion texture;
private FrameBuffer frameBuffer;

public CharacterActor(Character character) {
	this.character = character;
	setX(character.getX());
	setY(character.getY());
	String type = "player";
//	if (character.getType().hasAspect(CharacterAspect.HUMANOID)) {
	frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, true);
	updateTexture();
	texture = new TextureRegion(frameBuffer.getColorBufferTexture());
//	} else {
//		texture = atlasCharacters.findRegion(type);
//	}
	if (texture == null) {
		throw new RuntimeException("No image for character type " + type);
	}
}

public void updateTexture() {
	frameBuffer.begin();
	Gdx.gl.glClearColor(0, 0, 0, 0);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	batch.begin();
	batch.draw(atlasBodies.findRegion("human"), 0, 0, 32, 32);
	for (Item item : character.getEquipment()) {
		if (item.getType().isWearable()) {
			batch.draw(atlasApparel.findRegion(item.getType().getResourceName()), 0, 0);
		} else if (item.getType().isWieldable()) {
			batch.draw(atlasWielded.findRegion(item.getType().getResourceName()), 0, 0);
		}
	}
	batch.end();
	frameBuffer.end();
}

@Override
public void draw(SpriteBatch batch, float parentAlpha) {
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

private static class Batch extends SpriteBatch {
	private Batch() {
		OrthographicCamera camera = new OrthographicCamera(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
		camera.setToOrtho(true, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
		setProjectionMatrix(camera.combined);
	}
}
}
