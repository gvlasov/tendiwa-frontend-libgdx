package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.*;

public class FovEdgeOpaque extends TransitionPregenerator {
public final SpriteBatch batch;
private final ShaderProgram halfTransparencyShader;
private final ShaderProgram fovTransitionShader;

@Inject
public FovEdgeOpaque(
	@Named("transitions") TileTextureRegionProvider tileTextureRegionProvider,
	@Named("shader_half_transparency") ShaderProgram halfTransparencyShader,
	@Named("shader_fov_transition") ShaderProgram fovTransitionShader
) {
	super(tileTextureRegionProvider, 4);
	this.halfTransparencyShader = halfTransparencyShader;
	this.fovTransitionShader = fovTransitionShader;
	if (!this.fovTransitionShader.isCompiled()) {
		throw new RuntimeException(this.fovTransitionShader.getLog());
	}
	batch = new SpriteBatch();
	batch.setShader(this.fovTransitionShader);

	createTransitions();
}

protected Pixmap createTransition(CardinalDirection dir, final float opacity) {
	int diffusionDepth = 13;
	Pixmap.setBlending(Pixmap.Blending.None);
	Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(0, 0, 0, opacity);
	pixmap.fill();
	CardinalDirection opposite = dir.opposite();
	Rectangle transitionRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(dir).createRectangle(diffusionDepth);
	Rectangle clearRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(opposite).createRectangle(TILE_SIZE - diffusionDepth);
	pixmap.setColor(0, 0, 0, 0);
	// Fill the most of the pixmap with transparent pixels.
	pixmap.fillRectangle(clearRec.getX(), clearRec.getY(), clearRec.width(), clearRec.height());
	OrthoCellSegment sideSegment = transitionRec.getSideAsSegment(dir);
	BasicCell point = new BasicCell(sideSegment.getX(), sideSegment.getY());
	pixmap.setColor(0, 0, 0, 0);
	CardinalDirection dynamicGrowingDir = dir.isVertical() ? Directions.E : Directions.S;
	int startI = sideSegment.getStaticCoord();
	int oppositeGrowing = opposite.getGrowing();
	int iterationsI = 0;
	for (int i = startI;
	     i != startI + (diffusionDepth + 1) * opposite.getGrowing();
	     i += oppositeGrowing
		) {
		for (int j = sideSegment.min();
		     j <= sideSegment.max();
		     j += 1
			) {
			if (Chance.roll((i - startI) / oppositeGrowing * 100 / diffusionDepth + 10)) {
				// Discard pixel (set it to be transparent)
				pixmap.drawPixel(point.x(), point.y());
			}
			point = point.moveToSide(dynamicGrowingDir);
		}
		point = new BasicCell(sideSegment.getX(), sideSegment.getY()).moveToSide(opposite, iterationsI++);
	}
	return pixmap;
}

@Override
public Pixmap createTransition(CardinalDirection dir) {
	return createTransition(dir, 1.0f);
}
}
