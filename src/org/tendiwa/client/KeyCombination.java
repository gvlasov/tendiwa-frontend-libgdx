package org.tendiwa.client;

/**
 * Contains a keycode of the pressed key, and whether ctrl, shift of alt were pressed. Ctrl, Shift and Alt can't be
 * pressed at the same time. If two or more modifiers are pressed at the same time, then one is chosen by priority:
 * Shift > Atl > Ctrl.
 */
public class KeyCombination {
private final int keycode;
private boolean isCtrl = false;
private boolean isAlt = false;
private boolean isShift = false;

public KeyCombination(int keycode, boolean ctrl, boolean alt, boolean shift) {
	if (!(ctrl ^ alt ^ shift)) {
		throw new IllegalArgumentException("Only Ctrl, Alt or Shift can be used, no two of them at the same time.");
	}
	this.keycode = keycode;
}

public boolean isShift() {
	return isShift;
}

public boolean isCtrl() {
	return isCtrl;
}

public boolean isAlt() {
	return isAlt;
}

}
