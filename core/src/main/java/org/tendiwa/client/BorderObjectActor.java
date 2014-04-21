package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.Border;
import org.tendiwa.core.BorderObject;
import org.tendiwa.core.clients.RenderPlane;

public class BorderObjectActor extends Actor {
private final BorderObject borderObject;
private final RenderPlane renderPlane;
private final ShaderProgram drawWithRgb06Shader;
private final ShaderProgram defaultShader;
private final Border border;
private TextureAtlas.AtlasRegion atlasRegion;
private int worldPixelX;
private int worldPixelY;

@Inject
public BorderObjectActor(
	@Assisted Border border,
	@Assisted BorderObject borderObject,
	@Assisted RenderPlane renderPlane,
	@Named("shader_draw_with_rgb_06") ShaderProgram drawWithRgb06Shader,
	@Named("shader_default") ShaderProgram defaultShader
) {
	this.border = border;
	this.borderObject = borderObject;
	this.renderPlane = renderPlane;
	this.drawWithRgb06Shader = drawWithRgb06Shader;
	this.defaultShader = defaultShader;
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
		batch.setShader(drawWithRgb06Shader);
	}
	batch.draw(
		atlasRegion,
		worldPixelX,
		worldPixelY
	);
	if (shaderWasChanged) {
		batch.setShader(defaultShader);
	}
}
}
