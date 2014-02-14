package org.tendiwa.client.extensions.std;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.client.rendering.markings.MarkingsLayer;
import org.tendiwa.client.ui.actors.CursorActor;
import org.tendiwa.client.ui.cellSelection.CellSelectionActor;

@Singleton
public class StdMarkingsLayer extends MarkingsLayer {
@Inject
StdMarkingsLayer(
	@Named("game_screen_batch") Batch batch,
	GameScreenViewport viewport,
	CursorActor cursorActor,
	CellSelectionActor cellSelectionActor
) {
	super(batch, viewport);
	addActor(cursorActor);
	addActor(cellSelectionActor);
	cellSelectionActor.setVisible(false);
}
}
