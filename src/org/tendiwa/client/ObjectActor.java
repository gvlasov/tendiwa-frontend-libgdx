package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.core.GameObject;
import org.tendiwa.core.RenderPlane;

public class ObjectActor extends Actor {
private final int x;
private final int y;
private final GameObject gameObject;
private final RenderPlane renderPlane;

public ObjectActor(int x, int y, GameObject gameObject, RenderPlane renderPlane) {
	this.x = x;
	this.y = y;
	this.gameObject = gameObject;
	this.renderPlane = renderPlane;
	setX(x);
	setY(y);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean shaderWasChanged = false;
	if (renderPlane.isCellUnseen(x, y)) {
		shaderWasChanged = true;
		batch.setShader(GameScreen.drawWithRGB06Shader);
	}
	batch.draw(
		AtlasObjects.getInstance().findRegion(gameObject.getType().getResourceName()),
		x*GameScreen.TILE_SIZE,
		y*GameScreen.TILE_SIZE
	);
	if (shaderWasChanged) {
		batch.setShader(GameScreen.defaultShader);
	}
}
}
