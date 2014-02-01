package org.tendiwa.client.ui.uiModes;

import com.badlogic.gdx.InputProcessor;

public abstract class UiMode implements InputProcessor {
private UiModeManager manager;

void setModeManager(UiModeManager manager) {
	this.manager = manager;
}

public abstract void start();

protected void abort() {
	if (!manager.isModeLast(this)) {
		throw new RuntimeException("Can't abort mode that is not current");
	}
	manager.popMode();
}

public abstract void cleanup();
}
