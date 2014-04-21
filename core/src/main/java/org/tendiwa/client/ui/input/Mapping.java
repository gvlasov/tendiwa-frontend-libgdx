package org.tendiwa.client.ui.input;

public class Mapping {
private final KeyCombination combination;
private final NonPointerAction action;

Mapping(KeyCombination combination, NonPointerAction action) {
	this.combination = combination;
	this.action = action;
}

public KeyCombination getCombination() {
	return combination;
}

public NonPointerAction getAction() {
	return action;
}
}
