package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.core.Item;
import org.tendiwa.core.RenderPlane;

public class ItemActor extends Actor {
private final TextureRegion texture;
private final RenderPlane renderPlane;

public ItemActor(int x, int y, Item item, RenderPlane renderPlane) {
	super();
	this.renderPlane = renderPlane;
	texture = AtlasItems.getInstance().findRegion(item.getType().getResourceName());
	setX(x);
	setY(y);
	// To rotate the Actor around its center
	setOriginX(0.5f);
	setOriginY(0.5f);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	super.draw(batch, parentAlpha);
	boolean shaderWasChanged = false;
	if (renderPlane.isCellUnseen((int) getX(), (int) getY())) {
		shaderWasChanged = true;
		batch.setShader(GameScreen.drawWithRGB06Shader);
	}
	Color bufColor = batch.getColor();
	batch.setColor(getColor());
	batch.draw(
		texture,
		getX() * GameScreen.TILE_SIZE,
		getY() * GameScreen.TILE_SIZE,
		getOriginX() * GameScreen.TILE_SIZE,
		getOriginY() * GameScreen.TILE_SIZE,
		GameScreen.TILE_SIZE,
		GameScreen.TILE_SIZE,
		getScaleX(),
		getScaleY(),
		getRotation()
	);
	if (shaderWasChanged) {
		batch.setShader(GameScreen.defaultShader);
	}
	batch.setColor(bufColor);
}
}
