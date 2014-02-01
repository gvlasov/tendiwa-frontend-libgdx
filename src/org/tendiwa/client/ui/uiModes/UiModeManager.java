package org.tendiwa.client.ui.uiModes;

import com.badlogic.gdx.Gdx;

import java.util.Stack;

public class UiModeManager {
private Stack<UiMode> modes = new Stack<>();

public void pushMode(UiMode mode) {
	modes.push(mode);
	mode.start();
	Gdx.input.setInputProcessor(mode);
}

public boolean isModeLast(UiMode uiMode) {
	return modes.peek() == uiMode;
}

public void popMode() {
	modes.pop().cleanup();
	Gdx.input.setInputProcessor(modes.peek());

}
}
