package org.tendiwa.client.ui.controller;

import com.google.inject.Inject;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.factories.CellSelectionFactory;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.*;

public class ActionSelectionListener implements EntitySelectionListener<CharacterAbility> {
private final Volition volition;
private final UiModeManager uiModeManager;
private final CellSelectionFactory factory;

@Inject
public ActionSelectionListener(
	Volition volition,
	UiModeManager uiModeManager,
	CellSelectionFactory factory
) {
	this.volition = volition;
	this.uiModeManager = uiModeManager;
	this.factory = factory;
}

@Override
public void execute(final CharacterAbility characterAbility) {
	final ActionTargetType action = characterAbility.getAction();
	if (action instanceof ActionToCell) {
		CellSelection mode = factory.create(new EntitySelectionListener<EnhancedPoint>() {
			@Override
			public void execute(EnhancedPoint point) {
				volition.actionToCell((ActionToCell) action, point.x, point.y);
			}
		});
		uiModeManager.pushMode(mode);
	} else if (action instanceof ActionWithoutTarget) {
		volition.actionWithoutTarget((ActionWithoutTarget) action);
	}
}
}
