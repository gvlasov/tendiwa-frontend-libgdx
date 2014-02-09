package org.tendiwa.client.ui.input;

public class KeyboardInputMapping extends InputMapping {
private final int keyhash;

public KeyboardInputMapping(int keyhash) {

	this.keyhash = keyhash;
}

int getKeyhash() {
	return keyhash;
}
}
