package org.tendiwa.client.ui.factories;

import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.cellSelection.CellSelection;
import org.tendiwa.geometry.BasicCell;

public interface CellSelectionFactory {
public CellSelection create(
	EntitySelectionListener<BasicCell> entitySelectionListener,
    Runnable onExit
);
}
