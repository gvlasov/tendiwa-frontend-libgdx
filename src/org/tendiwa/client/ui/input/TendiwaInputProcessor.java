package org.tendiwa.client.ui.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.TaskManager;
import org.tendiwa.core.Server;

public class TendiwaInputProcessor implements InputProcessor {
private final TaskManager taskManager;
private final InputToActionMapper mapper;

@Inject
TendiwaInputProcessor(
	TaskManager taskManager,
	@Assisted InputToActionMapper mapper
) {
	this.taskManager = taskManager;
	this.mapper = mapper;
}

@Override
public boolean keyDown(int keycode) {
	if (keycode == Input.Keys.ESCAPE && taskManager.hasCurrentTask()) {
		taskManager.cancelCurrentTask();
	}
	if (Server.hasRequestToProcess()) {
		return false;
	}
	KeyboardAction action = mapper.getAction(keycode);
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
	if (taskManager.hasCurrentTask()) {
		return false;
	}
	MouseAction mouseAction = mapper.getMouseAction(button);
	if (mouseAction != null) {
		mouseAction.act(screenX, screenY);
		return true;
	}
	return false;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	return false;
}

@Override
public boolean scrolled(int amount) {
	return false;
}

public InputToActionMapper getMapper() {
	return mapper;
}
}

