package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.core.Character;
import org.tendiwa.core.*;
import org.tendiwa.core.events.EventPutOn;
import org.tendiwa.core.events.EventTakeOff;
import org.tendiwa.core.events.EventUnwield;
import org.tendiwa.core.events.EventWield;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

public class CharacterActor extends Actor {
private static final TextureAtlas atlasCharacters = AtlasCharacters.getInstance();
private static final TextureAtlas atlasBodies = new TextureAtlas(Gdx.files.internal("pack/bodies.atlas"), true);
private static final TextureAtlas atlasApparel = new TextureAtlas(Gdx.files.internal("pack/apparel.atlas"), true);
private static final TextureAtlas atlasWielded = new TextureAtlas(Gdx.files.internal("pack/wielded.atlas"), true);
private static final SpriteBatch batch = new OrthoBatch(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
private final Character character;
private final TextureRegion texture;
private FrameBuffer frameBuffer;

public CharacterActor(Observable model, Character character) {
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
	setZIndex(character.getY() * Tendiwa.getWorldWidth() + character.getX());
	model.subscribe(new Observer<EventPutOn>() {
		@Override
		public void update(EventPutOn event, EventEmitter<EventPutOn> emitter) {
			if (event.character == CharacterActor.this.character
				&& event.character.getType().hasAspect(CharacterAspect.HUMANOID)
				) {
				updateTexture();
			}
		}
	}, EventPutOn.class);
	model.subscribe(new Observer<EventTakeOff>() {
		@Override
		public void update(EventTakeOff event, EventEmitter<EventTakeOff> emitter) {
			if (event.character == CharacterActor.this.character
				&& event.character.getType().hasAspect(CharacterAspect.HUMANOID)
				) {
				updateTexture();
			}
		}
	}, EventTakeOff.class);
	model.subscribe(new Observer<EventWield>() {
		@Override
		public void update(EventWield event, EventEmitter<EventWield> emitter) {
			if (event.character == CharacterActor.this.character
				&& event.character.getType().hasAspect(CharacterAspect.HUMANOID)
				) {
				updateTexture();
			}
		}
	}, EventWield.class);
	model.subscribe(new Observer<EventUnwield>() {
		@Override
		public void update(EventUnwield event, EventEmitter<EventUnwield> emitter) {
			if (event.character == CharacterActor.this.character
				&& event.character.getType().hasAspect(CharacterAspect.HUMANOID)
				) {
				updateTexture();
			}
		}
	}, EventUnwield.class);
}

public Character getCharacter() {
	return character;
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
		(int) (getY() * GameScreen.TILE_SIZE) - GameScreen.TILE_SIZE / 3,
		getOriginX() * GameScreen.TILE_SIZE,
		getOriginY() * GameScreen.TILE_SIZE,
		GameScreen.TILE_SIZE,
		GameScreen.TILE_SIZE,
		getScaleX(),
		getScaleY(),
		getRotation()
	);
}

}
