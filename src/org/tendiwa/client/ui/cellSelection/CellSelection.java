package org.tendiwa.client.ui.cellSelection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.Cell;

public class CellSelection {
private final EntitySelectionListener<Cell> entitySelectionListener;
private final UiMode uiMode;
private final UiModeManager uiModeManager;
private final CellSelectionActionAdder actionAdder;
private final Runnable onExit;
private final CursorPosition model;

@Inject
public CellSelection(
	CursorPosition model,
	@Assisted EntitySelectionListener<Cell> entitySelectionListener,
	@Named("cell_selection") UiMode uiMode,
	UiModeManager uiModeManager,
	CellSelectionActionAdder actionAdder,
	@Assisted Runnable onExit
) {
	this.model = model;
	this.entitySelectionListener = entitySelectionListener;
	this.uiMode = uiMode;
	this.uiModeManager = uiModeManager;
	this.actionAdder = actionAdder;
	this.onExit = onExit;
}

void selectCurrentCell() {
	entitySelectionListener.execute(model.getPoint());
	exit();
}

public void start() {
	actionAdder.setCurrentSelection(this);
	uiModeManager.pushMode(uiMode);
}

public void exit() {
	onExit.run();
}
}
