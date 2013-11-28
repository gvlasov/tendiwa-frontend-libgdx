package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.RememberedItem;

public class ItemActor extends Actor {
private final TextureRegion texture;

public ItemActor(RememberedItem item) {
	texture = AtlasItems.getInstance().findRegion(item.getType().getName());
	setX(item.getX());
	setY(item.getY());
}

@Override
public void draw(SpriteBatch batch, float parentAlpha) {
	super.draw(batch, parentAlpha);
	boolean shaderWasChanged = false;
	if (GameScreen.getRenderWorld().isCellUnseen((int)getX(), (int)getY())) {
		shaderWasChanged = true;
		batch.setShader(GameScreen.drawWithRGB06Shader);
	}
	batch.draw(texture, getX() * GameScreen.TILE_SIZE, getY() * GameScreen.TILE_SIZE);
	if (shaderWasChanged) {
		batch.setShader(GameScreen.defaultShader);
	}
}
}
