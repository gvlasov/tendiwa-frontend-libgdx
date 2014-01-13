package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.tendiwa.core.*;
import org.tendiwa.core.meta.Chance;

public class FovEdgeOpaque extends TransitionPregenerator {
final SpriteBatch batch;
public final ShaderProgram halfTransparencyShader;
final ShaderProgram shader;
public FovEdgeOpaque() {
	super(4);
	shader = new ShaderProgram(
		SpriteBatch.createDefaultShader().getVertexShaderSource(),
		Gdx.files.internal("shaders/fovTransition.f.glsl").readString()
	);
	ShaderProgram.pedantic = false;
	if (!shader.isCompiled()) {
		Tendiwa.getLogger().error(shader.getLog());
	}
	batch = new SpriteBatch();
	batch.setShader(shader);

	halfTransparencyShader = GameScreen.createShader(Gdx.files.internal("shaders/fovHalfTransparency.f.glsl"));
	createTransitions();
}

protected Pixmap createTransition(CardinalDirection dir, final float opacity) {
	int diffusionDepth = 13;
	Pixmap.setBlending(Pixmap.Blending.None);
	Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(0, 0, 0, opacity);
	pixmap.fill();
	CardinalDirection opposite = dir.opposite();
	EnhancedRectangle transitionRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(dir).createRectangle(diffusionDepth);
	EnhancedRectangle clearRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(opposite).createRectangle(TILE_SIZE - diffusionDepth);
	pixmap.setColor(0, 0, 0, 0);
	// Fill the most of the pixmap with transparent pixels.
	pixmap.fillRectangle(clearRec.x, clearRec.y, clearRec.width, clearRec.height);
	Segment sideSegment = transitionRec.getSideAsSegment(dir);
	EnhancedPoint point = new EnhancedPoint(sideSegment.x, sideSegment.y);
	pixmap.setColor(0, 0, 0, 0);
	CardinalDirection dynamicGrowingDir = dir.isVertical() ? Directions.E : Directions.S;
	int startI = sideSegment.getStaticCoord();
	int oppositeGrowing = opposite.getGrowing();
	int iterationsI = 0;
	for (
		int i = startI;
		i != startI + (diffusionDepth + 1) * opposite.getGrowing();
		i += oppositeGrowing
		) {
		for (
			int j = sideSegment.getStartCoord();
			j <= sideSegment.getEndCoord();
			j += 1
			) {
			if (Chance.roll((i - startI) / oppositeGrowing * 100 / diffusionDepth + 10)) {
				// Discard pixel (set it to be transparent)
				pixmap.drawPixel(point.x, point.y);
			}
			point.moveToSide(dynamicGrowingDir);
		}
		point.setLocation(sideSegment.x, sideSegment.y);
		point.moveToSide(opposite, iterationsI++);
	}
	return pixmap;
}
@Override
public Pixmap createTransition(CardinalDirection dir) {
	return createTransition(dir, 1.0f);
}
}
