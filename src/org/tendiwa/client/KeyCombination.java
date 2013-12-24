package org.tendiwa.client;

import com.badlogic.gdx.Input;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a keycode of the pressed key, and whether ctrl, shift of alt were pressed. Ctrl, Shift and Alt can't be
 * pressed at the same time. If two or more modifiers are pressed at the same time, then one is chosen by priority:
 * Shift > Atl > Ctrl.
 */
public class KeyCombination {
private static Map<Character, Character> alphanumericToShifted = new HashMap<>();

static {
	alphanumericToShifted.put(',', '<');
	alphanumericToShifted.put('.', '>');
	alphanumericToShifted.put('/', '?');
	alphanumericToShifted.put(';', ':');
	alphanumericToShifted.put('\'', '\"');
	alphanumericToShifted.put('[', '{');
	alphanumericToShifted.put(']', '}');
	alphanumericToShifted.put('1', '!');
	alphanumericToShifted.put('2', '@');
	alphanumericToShifted.put('3', '#');
	alphanumericToShifted.put('4', '$');
	alphanumericToShifted.put('5', '%');
	alphanumericToShifted.put('6', '^');
	alphanumericToShifted.put('7', '&');
	alphanumericToShifted.put('8', '*');
	alphanumericToShifted.put('9', '(');
	alphanumericToShifted.put('0', ')');
	alphanumericToShifted.put('-', '_');
	alphanumericToShifted.put('=', '+');
	alphanumericToShifted.put('`', '~');
	alphanumericToShifted.put('\\', '|');
}

private final int keycode;
private boolean isCtrl = false;
private boolean isAlt = false;
private boolean isShift = false;

public KeyCombination(int keycode, boolean ctrl, boolean alt, boolean shift) {
	if (!(!ctrl && !alt && !shift) && !(ctrl ^ alt ^ shift)) {
		throw new IllegalArgumentException("Only Ctrl, Alt or Shift can be used, no two of them at the same time. " +
			"You used: ctrl " + ctrl + ", alt " + alt + ", shift " + shift);
	}
	this.keycode = keycode;
	isShift = shift;
	isCtrl = ctrl;
	isAlt = alt;
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

public String toString() {
	StringBuilder answer = new StringBuilder();
	String keyNameInLibgdx = Input.Keys.toString(keycode);
	String properKeyName;
	char firstChar = keyNameInLibgdx.charAt(0);
	boolean isKeyAlphanumeric = keyNameInLibgdx.length() == 1;
	if (isKeyAlphanumeric) {
		if (firstChar >= 'A' && firstChar <= 'Z') {
			properKeyName = Character.toString(isShift ? firstChar : Character.toLowerCase(firstChar));
		} else {
			assert alphanumericToShifted.containsKey(firstChar);
			properKeyName = Character.toString(alphanumericToShifted.get(firstChar));
		}
	} else {
		properKeyName = keyNameInLibgdx;
	}
	if (isCtrl) {
		answer.append("Ctrl + ");
	}
	if (isAlt) {
		answer.append("Alt + ");
	}
	if (isShift && !isKeyAlphanumeric) {
		answer.append("Shift + ");
	}
	answer.append(properKeyName);
	return answer.toString();
}

}
