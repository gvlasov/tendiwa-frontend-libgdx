package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Character;

public class CharacterActor extends Actor {
private static final TextureAtlas atlasCharacters = new TextureAtlas(Gdx.files.internal("pack/characters.atlas"), true);
private final Character character;
private final TextureRegion texture;

public CharacterActor(Character character) {
	this.character = character;
	setX(character.getX());
	setY(character.getY());
	String type = "player";
	texture = atlasCharacters.findRegion(type);
	if (texture == null) {
		throw new RuntimeException("No image for character type "+ type);
	}

}

@Override
public void draw(SpriteBatch batch, float parentAlpha) {
	batch.draw(texture, getX()*GameScreen.TILE_SIZE, getY()* GameScreen.TILE_SIZE);
}

}
