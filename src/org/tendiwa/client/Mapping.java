package org.tendiwa.client;

public class Mapping {
private final KeyCombination combination;
private final UiAction action;

Mapping(KeyCombination combination, UiAction action) {
	this.combination = combination;
	this.action = action;
}

public KeyCombination getCombination() {
	return combination;
}

public UiAction getAction() {
	return action;
}
}
