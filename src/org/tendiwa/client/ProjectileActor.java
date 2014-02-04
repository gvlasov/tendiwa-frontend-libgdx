package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.name.Named;
import org.tendiwa.core.Projectile;
import org.tendiwa.core.RenderPlane;

public class ProjectileActor extends Actor {
private final TextureAtlas.AtlasRegion texture;
private final RenderPlane renderPlane;
private final ShaderProgram drawWithRgb06Shader;
private final ShaderProgram defaultShader;

public ProjectileActor(
	Projectile projectile, int fromX, int fromY, int toX, int toY, RenderPlane renderPlane,
	@Named("shader_draw_with_rgb_06") ShaderProgram drawWithRgb06Shader,
	@Named("shader_default") ShaderProgram defaultShader
) {
	super();
	this.renderPlane = renderPlane;
	this.drawWithRgb06Shader = drawWithRgb06Shader;
	this.defaultShader = defaultShader;
	texture = AtlasProjectiles.getInstance().findRegion(projectile.getResourceName());
	assert texture != null : projectile.getResourceName();
	setX(fromX);
	setY(fromY);
	// To rotate the Actor around its center
	setOriginX(0.5f);
	setOriginY(0.5f);
	setRotation(MathUtils.atan2(toY - fromY, toX - fromX) * MathUtils.radiansToDegrees + 90);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	super.draw(batch, parentAlpha);
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
