package org.tendiwa.client.ui.cellSelection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import org.tendiwa.client.ui.model.CursorPosition;

public abstract class CellSelectionActor extends Actor {
protected final CursorPosition cursorPosition;

@Inject
CellSelectionActor(
	CursorPosition cursorPosition
) {
	this.cursorPosition = cursorPosition;
}
}