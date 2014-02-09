package org.tendiwa.client.ui.cellSelection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.client.ui.input.TendiwaInputProcessorFactory;
import org.tendiwa.client.ui.uiModes.UiMode;

@Singleton
public class CellSelectionUiModeProvider implements Provider<UiMode> {
private final TendiwaInputProcessorFactory tendiwaInputProcessorFactory;
/**
 * Modified at runtime with {@link CellSelectionActionAdder#setCurrentSelection(CellSelection)}.
 */
private final CellSelectionActionAdder actionsAdder;
private final InputToActionMapper mapper;

@Inject
CellSelectionUiModeProvider(
	TendiwaInputProcessorFactory tendiwaInputProcessorFactory,
	CellSelectionActionAdder actionsAdder,
    InputToActionMapper mapper
) {
	this.tendiwaInputProcessorFactory = tendiwaInputProcessorFactory;
	this.actionsAdder = actionsAdder;
	this.mapper = mapper;
}

@Override
public UiMode get() {
	return tendiwaInputProcessorFactory.create(mapper);
}
}
