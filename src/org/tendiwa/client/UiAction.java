package org.tendiwa.client;

import org.tendiwa.lexeme.Localizable;

public abstract class UiAction implements Localizable {
private final String name;

public UiAction(String name) {
	this.name = name;
}

public abstract void act();

@Override
public String getLocalizationId() {
	return name;
}
}
