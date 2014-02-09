package org.tendiwa.client.ui.input;

public abstract class ActionWithDefaultMapping extends NonPointerAction {

private final InputMapping defaultMapping;

protected ActionWithDefaultMapping(InputMapping defaultMapping, String localizationId) {
	super(localizationId);
	this.defaultMapping = defaultMapping;
}

public InputMapping getDefaultMapping() {
	return defaultMapping;
}
}
