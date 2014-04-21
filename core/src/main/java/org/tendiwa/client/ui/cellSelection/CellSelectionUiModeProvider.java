package org.tendiwa.client.ui.cellSelection;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.client.ui.input.TendiwaInputProcessorFactory;
import org.tendiwa.client.ui.uiModes.UiMode;

@Singleton
public class CellSelectionUiModeProvider implements Provider<UiMode> {
private final TendiwaInputProcessorFactory tendiwaInputProcessorFactory;
/**
 * Modified at runtime with {@link CellSelectionActionAdder#setCurrentSelection(CellSelection)}.
 */
private final InputToActionMapper mapper;
private final CellSelectionActionAdder actionAdder;

@Inject
CellSelectionUiModeProvider(
	TendiwaInputProcessorFactory tendiwaInputProcessorFactory,
	InputToActionMapper mapper,
    CellSelectionActionAdder actionAdder
) {
	this.tendiwaInputProcessorFactory = tendiwaInputProcessorFactory;
	this.mapper = mapper;
	this.actionAdder = actionAdder;
}

@Override
public UiMode get() {
	actionAdder.addTo(mapper);
	return tendiwaInputProcessorFactory.create(mapper);
}
}
