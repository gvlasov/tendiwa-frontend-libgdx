package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import tendiwa.core.CardinalDirection;

public class FovEdgeTransparent extends FovEdgeOpaque {
public FovEdgeTransparent() {
	super();
}

@Override
public Pixmap createTransition(CardinalDirection dir) {
	return super.createTransition(dir, 0.6f);
}
}
