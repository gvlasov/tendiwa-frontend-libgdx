package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.*;

import static org.tendiwa.geometry.GeometryPrimitives.cell;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

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
	Rectangle transitionRec = rectangle(TILE_SIZE, TILE_SIZE).side(dir).crust(diffusionDepth);
	Rectangle clearRec = rectangle(TILE_SIZE, TILE_SIZE).side(opposite).crust(TILE_SIZE - diffusionDepth);
	pixmap.setColor(0, 0, 0, 0);
	// Fill the most of the pixmap with transparent pixels.
	pixmap.fillRectangle(clearRec.x(), clearRec.y(), clearRec.width(), clearRec.height());
	OrthoCellSegment sideSegment = transitionRec.side(dir);
	Cell point = new BasicCell(sideSegment.getX(), sideSegment.getY());
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
		point = cell(sideSegment.getX(), sideSegment.getY()).moveToSide(opposite, iterationsI++);
	}
	return pixmap;
}

@Override
public Pixmap createTransition(CardinalDirection dir) {
	return createTransition(dir, 1.0f);
}
}
