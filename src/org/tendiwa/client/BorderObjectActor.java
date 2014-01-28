package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.core.Border;
import org.tendiwa.core.BorderObject;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;

public class BorderObjectActor extends Actor {
private final BorderObject borderObject;
private final Border border;
private TextureAtlas.AtlasRegion atlasRegion;
private int worldPixelX;
private int worldPixelY;

public BorderObjectActor(Border border, BorderObject borderObject) {
	this.border = border;
	this.borderObject = borderObject;
	setX(border.x);
	setY(border.y);
	atlasRegion = AtlasBorderObjects.getInstance().findRegion(borderObject.getType().getResourceName() + (border.side.isVertical() ? "_hor" : "_ver"));

	if (border.side.isVertical()) {
		worldPixelX = GameScreen.TILE_SIZE * border.x;
		worldPixelY = GameScreen.TILE_SIZE * border.y - atlasRegion.getRegionHeight();
	} else {
		assert border.side.isHorizontal();
		worldPixelX = GameScreen.TILE_SIZE * border.x;
		worldPixelY = GameScreen.TILE_SIZE * (border.y + 1) - atlasRegion.getRegionHeight();
	}

}

@Override
public void draw(Batch batch, float parentAlpha) {
	boolean shaderWasChanged = false;
	if (!TendiwaGame.getGameScreen().getRenderPlane().isBorderVisible(border)) {
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
