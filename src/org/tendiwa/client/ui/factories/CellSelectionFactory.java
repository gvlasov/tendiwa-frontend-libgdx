package org.tendiwa.client.ui.factories;

import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ui.controller.CellSelection;
import org.tendiwa.core.EnhancedPoint;

public interface CellSelectionFactory {
public CellSelection create(EntitySelectionListener<EnhancedPoint> entitySelectionListener);
}
