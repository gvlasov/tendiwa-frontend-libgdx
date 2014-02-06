package org.tendiwa.client.ui.controller;

import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.factories.CellSelectionFactory;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.*;

public class SpellSelectionListener implements EntitySelectionListener<Spell> {
private final Volition volition;
private final UiModeManager uiModeManager;
private final CellSelectionFactory factory;

SpellSelectionListener(
	Volition volition,
	UiModeManager uiModeManager,
	CellSelectionFactory factory
) {
	this.volition = volition;
	this.uiModeManager = uiModeManager;
	this.factory = factory;
}

@Override
public void execute(final Spell characterAbility) {
	final ActionTargetType action = characterAbility.getAction();
	if (action instanceof ActionToCell) {
		CellSelection cellSelection = factory.create(new EntitySelectionListener<EnhancedPoint>() {
			@Override
			public void execute(EnhancedPoint point) {
				volition.actionToCell((ActionToCell) action, point.x, point.y);
			}
		});
		uiModeManager.pushMode(cellSelection);
	} else if (action instanceof ActionWithoutTarget) {
		volition.actionWithoutTarget((ActionWithoutTarget) action);
	}
}
}
