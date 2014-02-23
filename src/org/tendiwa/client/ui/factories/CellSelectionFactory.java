package org.tendiwa.client.ui.factories;

import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.cellSelection.CellSelection;
import org.tendiwa.core.Cell;

public interface CellSelectionFactory {
public CellSelection create(
	EntitySelectionListener<Cell> entitySelectionListener,
    Runnable onExit
);
}
