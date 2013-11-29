package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Item;

public class ItemActor extends Actor {
private final TextureRegion texture;

public ItemActor(int x, int y, Item item) {
	texture = AtlasItems.getInstance().findRegion(item.getType().getName());
	setX(x);
	setY(y);
}

@Override
public void draw(SpriteBatch batch, float parentAlpha) {
	super.draw(batch, parentAlpha);
	boolean shaderWasChanged = false;
	if (GameScreen.getRenderWorld().isCellUnseen((int) getX(), (int) getY())) {
		shaderWasChanged = true;
		batch.setShader(GameScreen.drawWithRGB06Shader);
	}
	Color bufColor = batch.getColor();
	batch.setColor(getColor());
	batch.draw(texture, getX() * GameScreen.TILE_SIZE, getY() * GameScreen.TILE_SIZE);
	if (shaderWasChanged) {
		batch.setShader(GameScreen.defaultShader);
	}
	batch.setColor(bufColor);
}
}
