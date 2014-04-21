package org.tendiwa.client.ui.input;

public class MouseInputMapping extends InputMapping {
private final int buttonHash;

public MouseInputMapping(int buttonHash) {

	this.buttonHash = buttonHash;
}

int getButtonHash() {
	return buttonHash;
}
}
