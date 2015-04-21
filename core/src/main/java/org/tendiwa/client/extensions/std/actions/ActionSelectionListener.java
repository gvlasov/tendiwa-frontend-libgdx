package org.tendiwa.client.extensions.std.actions;

import com.google.inject.Inject;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.actors.CursorActor;
import org.tendiwa.client.ui.cellSelection.CellSelectionActor;
import org.tendiwa.client.ui.factories.CellSelectionFactory;
import org.tendiwa.core.*;
import org.tendiwa.core.volition.Volition;
import org.tendiwa.geometry.BasicCell;

public class ActionSelectionListener implements EntitySelectionListener<CharacterAbility> {
private final Volition volition;
private final CellSelectionFactory factory;
private final CellSelectionActor cellSelectionActor;
private final CursorActor cursorActor;

@Inject
public ActionSelectionListener(
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
public void execute(final CharacterAbility characterAbility) {
	final ActionTargetType action = characterAbility.getAction();
	if (action instanceof ActionToCell) {
		cellSelectionActor.setVisible(true);
		cursorActor.setVisible(false);
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
					cellSelectionActor.setVisible(false);
					cursorActor.setVisible(true);
				}
			}
		).start();
	} else if (action instanceof ActionWithoutTarget) {
		volition.actionWithoutTarget((ActionWithoutTarget) action);
	}
}
}
