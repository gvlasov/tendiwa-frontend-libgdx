package org.tendiwa.client.ui.cellSelection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.EnhancedPoint;

public class CellSelection {
private final EntitySelectionListener<EnhancedPoint> entitySelectionListener;
private final UiMode uiMode;
private final UiModeManager uiModeManager;
private final CursorPosition model;

@Inject
public CellSelection(
	CursorPosition model,
	@Assisted EntitySelectionListener<EnhancedPoint> entitySelectionListener,
	@Named("cell_selection") UiMode uiMode,
	UiModeManager uiModeManager
) {
	this.model = model;
	this.entitySelectionListener = entitySelectionListener;
	this.uiMode = uiMode;
	this.uiModeManager = uiModeManager;
}

void selectCurrentCell() {
	entitySelectionListener.execute(model.getPoint());
}

public void start() {
	uiModeManager.pushMode(uiMode);
}
}
