package org.tendiwa.client.ui.controller;

import com.google.inject.Inject;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.actors.CursorActor;
import org.tendiwa.client.ui.cellSelection.CellSelectionActor;
import org.tendiwa.client.ui.factories.CellSelectionFactory;
import org.tendiwa.core.*;
import org.tendiwa.core.volition.Volition;
import org.tendiwa.geometry.BasicCell;

public class SpellSelectionListener implements EntitySelectionListener<Spell> {
private final Volition volition;
private final CellSelectionFactory factory;
private final CellSelectionActor cellSelectionActor;
private final CursorActor cursorActor;

@Inject
SpellSelectionListener(
	Volition volition,
	CellSelectionFactory factory,
    CellSelectionActor cellSelectionActor,
    CursorActor cursorActor
) {
	this.volition = volition;
	this.factory = factory;
	this.cellSelectionActor = cellSelectionActor;
	this.cursorActor = cursorActor;
}

@Override
public void execute(final Spell characterAbility) {
	final ActionTargetType action = characterAbility.getAction();
	if (action instanceof ActionToCell) {
		cursorActor.setVisible(false);
		cellSelectionActor.setVisible(true);
		factory.create(
			new EntitySelectionListener<BasicCell>() {
				@Override
				public void execute(BasicCell point) {
					volition.actionToCell((ActionToCell) action, point.x(), point.y());
				}
			},
			new Runnable() {
				@Override
				public void run() {
					cursorActor.setVisible(true);
					cellSelectionActor.setVisible(false);
				}
			}
		).start();
	} else if (action instanceof ActionWithoutTarget) {
		volition.actionWithoutTarget((ActionWithoutTarget) action);
	}
}
}
