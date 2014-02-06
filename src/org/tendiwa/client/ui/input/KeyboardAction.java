package org.tendiwa.client.ui.input;

public abstract class KeyboardAction extends UiAction {
public KeyboardAction(String localizationId) {
	super(localizationId);
}
public abstract void act();
}
