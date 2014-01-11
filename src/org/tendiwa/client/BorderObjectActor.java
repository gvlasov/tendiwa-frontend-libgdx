package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import tendiwa.core.BorderObject;
import tendiwa.core.CardinalDirection;
import tendiwa.core.Directions;

public class BorderObjectActor extends Actor {
private final int x;
private final int y;
private final BorderObject borderObject;

public BorderObjectActor(int x, int y, CardinalDirection side, BorderObject borderObject) {
	assert side != null;
	if (side != Directions.N && side != Directions.W) {
		if (side == Directions.E) {
			side = Directions.W;
			x += 1;
		} else {
			assert side == Directions.S;
			side = Directions.N;
			y += 1;
		}
	}
	this.x = x;
	this.y = y;
	this.borderObject = borderObject;
	setX(x);
	setY(y);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean shaderWasChanged = false;
	if (TendiwaGame.getGameScreen().getRenderPlane().isCellUnseen(x, y)) {
		shaderWasChanged = true;
		batch.setShader(GameScreen.drawWithRGB06Shader);
	}
	batch.draw(
		AtlasObjects.getInstance().findRegion(borderObject.getType().getResourceName()),
		x * GameScreen.TILE_SIZE,
		y * GameScreen.TILE_SIZE
	);
	if (shaderWasChanged) {
		batch.setShader(GameScreen.defaultShader);
	}
}
}
