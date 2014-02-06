package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.CardinalDirection;

public class FovEdgeTransparent extends FovEdgeOpaque {
@Inject
public FovEdgeTransparent(
	@Named("transitions") TileTextureRegionProvider tileTextureRegionProvider,
	@Named("shader_half_transparency") ShaderProgram halfTransparencyShader,
	@Named("shader_fov_transition") ShaderProgram fovTransitionShader
) {
	super(tileTextureRegionProvider, halfTransparencyShader, fovTransitionShader);
	createTransitions();
}

@Override
public Pixmap createTransition(CardinalDirection dir) {
	return super.createTransition(dir, 0.6f);
}
}
