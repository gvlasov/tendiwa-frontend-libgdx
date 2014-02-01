package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.core.*;

public class BorderObjectActor extends Actor {
private final BorderObject borderObject;
private final RenderPlane renderPlane;
private final Border border;
private TextureAtlas.AtlasRegion atlasRegion;
private int worldPixelX;
private int worldPixelY;

public BorderObjectActor(Border border, BorderObject borderObject, RenderPlane renderPlane) {
	this.border = border;
	this.borderObject = borderObject;
	this.renderPlane = renderPlane;
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
	if (!renderPlane.isBorderVisible(border)) {
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
