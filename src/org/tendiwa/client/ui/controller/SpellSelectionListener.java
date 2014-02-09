package org.tendiwa.client.ui.controller;

import com.google.inject.Inject;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.factories.CellSelectionFactory;
import org.tendiwa.core.*;

public class SpellSelectionListener implements EntitySelectionListener<Spell> {
private final Volition volition;
private final CellSelectionFactory factory;

@Inject
SpellSelectionListener(
	Volition volition,
	CellSelectionFactory factory
) {
	this.volition = volition;
	this.factory = factory;
}

@Override
public void execute(final Spell characterAbility) {
	final ActionTargetType action = characterAbility.getAction();
	if (action instanceof ActionToCell) {
		factory.create(new EntitySelectionListener<EnhancedPoint>() {
			@Override
			public void execute(EnhancedPoint point) {
				volition.actionToCell((ActionToCell) action, point.x, point.y);
			}
		}).start();
	} else if (action instanceof ActionWithoutTarget) {
		volition.actionWithoutTarget((ActionWithoutTarget) action);
	}
}
}
