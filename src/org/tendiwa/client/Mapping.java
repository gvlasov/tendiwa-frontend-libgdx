package org.tendiwa.client;

class Mapping {
private final KeyCombination combination;
private final UiAction action;

Mapping(KeyCombination combination, UiAction action) {
	this.combination = combination;
	this.action = action;
}

KeyCombination getCombination() {
	return combination;
}

UiAction getAction() {
	return action;
}
}
