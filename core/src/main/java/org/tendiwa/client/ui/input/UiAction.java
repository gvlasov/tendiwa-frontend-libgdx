package org.tendiwa.client.ui.input;

import org.tendiwa.lexeme.Localizable;

public abstract class UiAction implements Localizable {
private final String localizationId;

public UiAction(String localizationId) {
	this.localizationId = localizationId;
}

@Override
public String getLocalizationId() {
	return localizationId;
}
}
