package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.GameObject;
import org.tendiwa.core.clients.RenderPlane;

public class ObjectActor extends Actor {
private final int x;
private final int y;
private final GameObject gameObject;
private final RenderPlane renderPlane;
private final ShaderProgram drawWithRgb06Shader;
private final ShaderProgram defaultShader;

@Inject
public ObjectActor(
	@Assisted("x") int x,
	@Assisted("y") int y,
	@Assisted GameObject gameObject,
	@Assisted RenderPlane renderPlane,
	@Named("shader_draw_with_rgb_06") ShaderProgram drawWithRgb06Shader,
	@Named("shader_default") ShaderProgram defaultShader
) {
	this.x = x;
	this.y = y;
	this.gameObject = gameObject;
	this.renderPlane = renderPlane;
	this.drawWithRgb06Shader = drawWithRgb06Shader;
	this.defaultShader = defaultShader;
	setX(x);
	setY(y);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean shaderWasChanged = false;
	if (renderPlane.isCellUnseen(x, y)) {
		shaderWasChanged = true;
		batch.setShader(drawWithRgb06Shader);
	}
	batch.draw(
		AtlasObjects.getInstance().findRegion(gameObject.getType().getResourceName()),
		x * GameScreen.TILE_SIZE,
		y * GameScreen.TILE_SIZE
	);
	if (shaderWasChanged) {
		batch.setShader(defaultShader);
	}
}
}
