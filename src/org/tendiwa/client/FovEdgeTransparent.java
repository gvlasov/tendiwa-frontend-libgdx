package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import org.tendiwa.core.CardinalDirection;

public class FovEdgeTransparent extends FovEdgeOpaque {
public FovEdgeTransparent() {
	super();
	createTransitions();
}

@Override
public Pixmap createTransition(CardinalDirection dir) {
	return super.createTransition(dir, 0.6f);
}
}
