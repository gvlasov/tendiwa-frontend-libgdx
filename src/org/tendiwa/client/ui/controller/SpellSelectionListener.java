package org.tendiwa.client.ui.controller;

import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.ui.actors.CellSelectionActor;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.*;

public class SpellSelectionListener implements EntitySelectionListener<Spell> {
private final Volition volition;
private final GameScreen gameScreen;
private final CursorPosition cursorPosition;
private final CellSelectionActor cellSelectionActor;
private final UiModeManager uiModeManager;

SpellSelectionListener(Volition volition, GameScreen gameScreen, CursorPosition cursorPosition, CellSelectionActor cellSelectionActor, UiModeManager uiModeManager) {
	this.volition = volition;
	this.gameScreen = gameScreen;
	this.cursorPosition = cursorPosition;
	this.cellSelectionActor = cellSelectionActor;
	this.uiModeManager = uiModeManager;
}

@Override
public void execute(final Spell characterAbility) {
	final ActionTargetType action = characterAbility.getAction();
	if (action instanceof ActionToCell) {
		uiModeManager.pushMode(
			new CellSelection(gameScreen, cursorPosition, cellSelectionActor, new EntitySelectionListener<EnhancedPoint>() {

				@Override
				public void execute(EnhancedPoint point) {
					volition.actionToCell((ActionToCell) action, point.x, point.y);
				}
			})
		);
	} else if (action instanceof ActionWithoutTarget) {
		volition.actionWithoutTarget((ActionWithoutTarget) action);
	}
}
}
