package org.tendiwa.client.ui.input;

public abstract class MouseAction extends UiAction {
public MouseAction(String localizationId) {
	super(localizationId);
}
public abstract void act(int clickPixelX, int clickPixelY);
}
