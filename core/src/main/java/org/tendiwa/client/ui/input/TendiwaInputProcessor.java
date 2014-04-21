package org.tendiwa.client.ui.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.TaskManager;
import org.tendiwa.client.ui.TendiwaUiStage;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.core.Server;
import org.tendiwa.core.observation.ThreadProxy;

public class TendiwaInputProcessor implements UiMode {
private final ThreadProxy model;
private final TaskManager taskManager;
private final InputToActionMapper mapper;
private final Stage ui;
private final Server server;

@Inject
TendiwaInputProcessor(
	ThreadProxy model,
	TaskManager taskManager,
	@Assisted InputToActionMapper mapper,
	TendiwaUiStage ui,
    Server server
) {
	this.model = model;
	this.taskManager = taskManager;
	this.mapper = mapper;
	this.ui = ui;
	this.server = server;
}

@Override
public boolean keyDown(int keycode) {
	if (keycode == Input.Keys.ESCAPE && taskManager.hasCurrentTask()) {
		taskManager.cancelCurrentTask();
	}
	if (server.hasRequestToProcess() || !model.areAllEmittersCheckedOut()) {
		return false;
	}
	NonPointerAction action = mapper.getAction(keycode);
	if (action != null) {
		action.act();
		return true;
	}
	return false;
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	return false;
}

@Override
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	if (taskManager.hasCurrentTask()) {
		return false;
	}
	if (ui.touchDown(screenX, screenY, pointer, button)) {
		return true;
	}
	MouseAction mouseAction = mapper.getMouseAction(button);
	if (mouseAction == null) {
		return false;
	}
	mouseAction.act(screenX, screenY);
	return true;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	MouseAction mouseMoveAction = mapper.getMouseMoveAction();
	if (mouseMoveAction == null) {
		return false;
	}
	mouseMoveAction.act(screenX, screenY);
	return true;
}

@Override
public boolean scrolled(int amount) {
	return false;
}

@Override
public InputToActionMapper getMapper() {
	return mapper;
}

@Override
public void addMappings(ActionsAdder adder) {
	adder.addTo(mapper);
}

}

