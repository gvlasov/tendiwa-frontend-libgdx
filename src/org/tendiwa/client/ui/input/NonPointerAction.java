package org.tendiwa.client.ui.input;

public abstract class NonPointerAction extends UiAction {
NonPointerAction(String localizationId) {
	super(localizationId);
}

public abstract void act();
}
