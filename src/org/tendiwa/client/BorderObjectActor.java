package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.core.BorderObject;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;

public class BorderObjectActor extends Actor {
private final int x;
private final int y;
private final CardinalDirection side;
private final BorderObject borderObject;
private TextureAtlas.AtlasRegion atlasRegion;
private int worldPixelX;
private int worldPixelY;

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
	this.side = side;
	this.x = x;
	this.y = y;
	this.borderObject = borderObject;
	setX(x);
	setY(y);
	atlasRegion = AtlasBorderObjects.getInstance().findRegion(borderObject.getType().getResourceName() + (side.isVertical() ? "_hor" : "_ver"));

	if (side.isVertical()) {
		worldPixelX = GameScreen.TILE_SIZE * x;
		worldPixelY = GameScreen.TILE_SIZE * y - atlasRegion.getRegionHeight();
	} else {
		assert side.isHorizontal();
		worldPixelX = GameScreen.TILE_SIZE * x;
		worldPixelY = GameScreen.TILE_SIZE * (y + 1) - atlasRegion.getRegionHeight();
	}

}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean shaderWasChanged = false;
	if (TendiwaGame.getGameScreen().getRenderPlane().isCellUnseen(x, y)) {
		shaderWasChanged = true;
		batch.setShader(GameScreen.drawWithRGB06Shader);
	}
	batch.draw(
		atlasRegion,
		worldPixelX,
		worldPixelY
	);
	if (shaderWasChanged) {
		batch.setShader(GameScreen.defaultShader);
	}
}
}
