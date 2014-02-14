package org.tendiwa.client.rendering.markings;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.inject.name.Named;
import org.tendiwa.client.GameScreenViewport;

public abstract class MarkingsLayer extends Stage {
protected MarkingsLayer(
	@Named("game_screen_batch") Batch batch,
	GameScreenViewport viewport
) {
	super(viewport.getWindowWidthPixels(), viewport.getWindowHeightPixels(), true, batch);
	setCamera(viewport.getCamera());
}
}
