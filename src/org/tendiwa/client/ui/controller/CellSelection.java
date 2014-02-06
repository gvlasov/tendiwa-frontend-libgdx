package org.tendiwa.client.ui.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.client.ui.actors.CellSelectionActor;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.core.EnhancedPoint;

public class CellSelection extends UiMode {
private final EntitySelectionListener<EnhancedPoint> entitySelectionListener;
private final Input gdxInput;
private final GameScreenViewport viewport;
private final CursorPosition model;
private final CellSelectionActor view;

@Inject
public CellSelection(
	GameScreenViewport viewport,
	CursorPosition model,
	CellSelectionActor view,
	@Assisted EntitySelectionListener<EnhancedPoint> entitySelectionListener,
	Input gdxInput
) {
	this.viewport = viewport;
	this.model = model;
	this.view = view;
	this.entitySelectionListener = entitySelectionListener;
	this.gdxInput = gdxInput;
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
		selectCurrentCell();
	} else {
		return false;
	}
	return true;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	selectCurrentCell();
	return true;
}

private void selectCurrentCell() {
	gdxInput.setInputProcessor(defaultInputProcessor);
	entitySelectionListener.execute(model.getPoint());
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
	EnhancedPoint point = viewport.screenPixelToWorldCell(screenX, screenY);
	model.setPoint(point);
	view.setWorldCoordinates(point.x, point.y);
	return true;
}

@Override
public boolean scrolled(int amount) {
	return false;
}
}
