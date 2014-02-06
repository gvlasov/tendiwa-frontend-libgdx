package org.tendiwa.client.ui.input;

public class Mapping {
private final KeyCombination combination;
private final KeyboardAction action;

Mapping(KeyCombination combination, KeyboardAction action) {
	this.combination = combination;
	this.action = action;
}

public KeyCombination getCombination() {
	return combination;
}

public KeyboardAction getAction() {
	return action;
}
}
