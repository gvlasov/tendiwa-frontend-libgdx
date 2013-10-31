package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Character;

public class CharacterActor extends Actor {
private static final TextureAtlas atlasCharacters = new TextureAtlas("pack/characters.atlas");
private final Character character;
private final TextureRegion texture;

public CharacterActor(Character character) {
	this.character = character;
	String type = "player";
	texture = atlasCharacters.findRegion(type);
	if (texture == null) {
		throw new RuntimeException("No image for character type "+ type);
	}

}

@Override
public void draw(SpriteBatch batch, float parentAlpha) {
	batch.draw(texture, character.getX() * GameScreen.TILE_SIZE, character.getY() * GameScreen.TILE_SIZE);
}
}
