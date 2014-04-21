package org.tendiwa.client.ui.uiModes;

import com.badlogic.gdx.Input;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Stack;

@Singleton
public class UiModeManager {
private final Input gdxInput;
private Stack<UiMode> modes = new Stack<>();

@Inject
UiModeManager(
	Input gdxInput
) {
	this.gdxInput = gdxInput;
}

public void pushMode(UiMode mode) {
	modes.push(mode);
	gdxInput.setInputProcessor(mode);
}

public void popMode() {
	modes.pop();
	gdxInput.setInputProcessor(modes.peek());
}

public UiMode getCurrentMode() {
	return modes.peek();
}
}
