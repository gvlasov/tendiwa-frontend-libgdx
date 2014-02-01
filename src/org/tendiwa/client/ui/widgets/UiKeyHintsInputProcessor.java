package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class UiKeyHintsInputProcessor implements InputProcessor {
private final Runnable onEscape;

UiKeyHintsInputProcessor(Runnable onEscape) {

	this.onEscape = onEscape;
}

@Override
public boolean keyDown(int keycode) {
	if (keycode == Input.Keys.ESCAPE) {
		onEscape.run();
	}
	return true;
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
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
}
