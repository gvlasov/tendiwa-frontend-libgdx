package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.Projectile;

public class ProjectileActor extends Actor {
private final TextureAtlas.AtlasRegion texture;

public ProjectileActor(Projectile projectile, int x, int y) {
	super();
	texture = AtlasProjectiles.getInstance().findRegion(projectile.getResourceName());
	assert texture != null;
	setX(x);
	setY(y);
	// To rotate the Actor around its center
	setOriginX(0.5f);
	setOriginY(0.5f);
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
