package org.tendiwa.client.ui.input;

import com.badlogic.gdx.Input;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import java.util.*;

public class InputToActionMapper implements Iterable<Mapping> {
private final Input gdxInput;
private Map<KeyCombination, NonPointerAction> combinationToAction = new HashMap<>();
/**
 * Contains same data as {@link InputToActionMapper#combinationToAction}, but in form of list (so it has a defined order
 * and is generally easier to iterate over).
 */
private List<Mapping> mappings = new LinkedList<>();
private Map<Integer, MouseAction> mouseActions = new HashMap<>();
private MouseAction mouseMovedAction;

@Inject
InputToActionMapper(
	Input gdxInput
) {
	this.gdxInput = gdxInput;
}

/**
 * Maps a key combination to an action so action will be executed when that key combination is pressed.
 *
 * @param combination
 * 	An integer which is a sum of 4 parameters: <ul><li>Keycode (from {@link Input.Keys})</li><li>{@code isCtrl ? 1 << 8
 * 	: 0}</li><li>{@code isAlt ? 1 << 9 : 0}</li><li>{@code isShift ? 1 << 10 : 0}</li></ul>
 */
public void putAction(int combination, NonPointerAction action) {
	Mapping mapping = new Mapping(KeyCombinationPool.obtainCombination(combination), action);
	assert !combinationToAction.containsKey(mapping.getCombination());
	combinationToAction.put(mapping.getCombination(), mapping.getAction());
	mappings.add(mapping);
}

public void putMouseAction(int button, MouseAction action) {
	assert !mouseActions.containsKey(button);
	mouseActions.put(button, action);
}

public ImmutableList<Mapping> getMappings() {
	return ImmutableList.copyOf(mappings);
}

public NonPointerAction getAction(int keycode) {
	switch (keycode) {
		case Input.Keys.SHIFT_LEFT:
		case Input.Keys.SHIFT_RIGHT:
		case Input.Keys.ALT_LEFT:
		case Input.Keys.ALT_RIGHT:
		case Input.Keys.CONTROL_LEFT:
		case Input.Keys.CONTROL_RIGHT:
			return null;
	}
	KeyCombination combination = KeyCombinationPool.obtainCombination(
		keycode,
		gdxInput.isKeyPressed(Input.Keys.CONTROL_LEFT) || gdxInput.isKeyPressed(Input.Keys.CONTROL_RIGHT),
		gdxInput.isKeyPressed(Input.Keys.ALT_LEFT) || gdxInput.isKeyPressed(Input.Keys.ALT_RIGHT),
		gdxInput.isKeyPressed(Input.Keys.SHIFT_LEFT) || gdxInput.isKeyPressed(Input.Keys.SHIFT_RIGHT)
	);
	NonPointerAction action = combinationToAction.get(combination);
	if (action == null) {
		return null;
	} else {
		return action;
	}

}

public MouseAction getMouseAction(int button) {
	return mouseActions.get(button);
}

@Override
public Iterator<Mapping> iterator() {
	return mappings.iterator();
}

public void putMouseMovedAction(MouseAction mouseAction) {
	mouseMovedAction = mouseAction;
}

public MouseAction getMouseMoveAction() {
	return mouseMovedAction;
}

public void addMapping(InputMapping mapping, NonPointerAction action) {
	if (mapping instanceof KeyboardInputMapping) {
		putAction(((KeyboardInputMapping) mapping).getKeyhash(), action);
	}
}

private static class KeyCombinationPool {
	static Map<Integer, KeyCombination> combinations = new HashMap<>();

	static KeyCombination obtainCombination(int keycode, boolean ctrl, boolean alt, boolean shift) {
		int compositeKeyCode = computeCompositeKeyCode(keycode, ctrl, alt, shift);
		if (combinations.containsKey(compositeKeyCode)) {
			return combinations.get(compositeKeyCode);
		} else {
			KeyCombination answer = new KeyCombination(keycode, ctrl, alt, shift);
			combinations.put(compositeKeyCode, answer);
			return answer;
		}
	}

	static int computeCompositeKeyCode(int keycode, boolean ctrl, boolean alt, boolean shift) {
		return keycode
			+ (ctrl ? Modifiers.ctrl : 0)
			+ (alt ? Modifiers.alt : 0)
			+ (shift ? Modifiers.shift : 0);
	}

	static KeyCombination obtainCombination(int combination) {
		return obtainCombination(
			combination % (Modifiers.ctrl),
			(combination & Modifiers.ctrl) == Modifiers.ctrl,
			(combination & Modifiers.alt) == Modifiers.alt,
			(combination & Modifiers.shift) == Modifiers.shift
		);
	}
}
}
