package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.Item;
import org.tendiwa.core.clients.RenderPlane;

public class ItemActor extends Actor {
private final TextureRegion texture;
private final RenderPlane renderPlane;
private final ShaderProgram drawWithRgb06Shader;
private final ShaderProgram defaultShader;

@Inject
public ItemActor(
	@Assisted("x") int x,
	@Assisted("y") int y,
	@Assisted Item item,
	@Assisted RenderPlane renderPlane,
	@Named("shader_draw_with_rgb_06") ShaderProgram drawWithRgb06Shader,
	@Named("shader_default") ShaderProgram defaultShader
) {
	super();
	this.renderPlane = renderPlane;
	this.drawWithRgb06Shader = drawWithRgb06Shader;
	this.defaultShader = defaultShader;
	texture = AtlasItems.getInstance().findRegion(item.getType().getResourceName());
	setX(x);
	setY(y);
	// To rotate the Actor around its center
	setOriginX(0.5f);
	setOriginY(0.5f);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean shaderWasChanged = false;
	if (renderPlane.isCellUnseen((int) getX(), (int) getY())) {
		shaderWasChanged = true;
		batch.setShader(drawWithRgb06Shader);
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
		batch.setShader(defaultShader);
	}
	batch.setColor(bufColor);
}
}
