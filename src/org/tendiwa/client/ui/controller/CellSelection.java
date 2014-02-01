package org.tendiwa.client.ui.controller;

import com.badlogic.gdx.Input;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.actors.CellSelectionActor;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.core.EnhancedPoint;

public class CellSelection extends UiMode {
private final EntitySelectionListener<EnhancedPoint> entitySelectionListener;
private final CursorPosition model;
private final CellSelectionActor view;
private GameScreen gameScreen;

/**
 * Adds a listener that will wait for a cell to be selected with {@link CursorPosition#selectCurrentCell()}.
 *
 * @param entitySelectionListener
 * 	A listener that will wait for a cell to be selected.
 */
public CellSelection(GameScreen gameScreen, CursorPosition model, CellSelectionActor view, EntitySelectionListener<EnhancedPoint> entitySelectionListener) {
	this.gameScreen = gameScreen;
	this.model = model;
	this.view = view;
	this.entitySelectionListener = entitySelectionListener;
}

@Override
public void start() {

}

@Override
public void cleanup() {

}

@Override
public boolean keyDown(int keycode) {
	if (keycode == Input.Keys.ESCAPE) {
		abort();
	}
	return true;
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	if (character == 'h') {
		model.moveCursorBy(-1, 0);
	} else if (character == 'j') {
		model.moveCursorBy(0, 1);
	} else if (character == 'k') {
		model.moveCursorBy(0, -1);
	} else if (character == 'l') {
		model.moveCursorBy(1, 0);
	} else if (character == 'y') {
		model.moveCursorBy(-1, -1);
	} else if (character == 'u') {
		model.moveCursorBy(1, -1);
	} else if (character == 'b') {
		model.moveCursorBy(-1, 1);
	} else if (character == 'n') {
		model.moveCursorBy(1, 1);
	} else if (character == 'f' || character == ' ') {
		entitySelectionListener.execute(model.getPoint());
	} else {
		return false;
	}
	return true;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	entitySelectionListener.execute(model.getPoint());
	return true;
}

@Override
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	return true;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	EnhancedPoint point = gameScreen.screenPixelToWorldCell(screenX, screenY);
	model.setPoint(point);
	view.setWorldCoordinates(point.x, point.y);
	return true;
}

@Override
public boolean scrolled(int amount) {
	return false;
}
}
