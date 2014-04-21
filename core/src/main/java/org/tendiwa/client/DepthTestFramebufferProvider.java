package org.tendiwa.client;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DepthTestFramebufferProvider implements Provider<FrameBuffer> {
private final GameScreenViewport viewport;

@Inject
public DepthTestFramebufferProvider(GameScreenViewport viewport) {

	this.viewport = viewport;
}
@Override
public FrameBuffer get() {
	return new FrameBuffer(
		Pixmap.Format.RGBA8888,
		viewport.getWindowWidthPixels(),
		viewport.getWindowHeightPixels(),
		true
	);
}
}
