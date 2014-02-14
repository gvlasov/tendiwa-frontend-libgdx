package org.tendiwa.client.ui.input;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.TendiwaUiStage;
import org.tendiwa.client.ui.uiModes.UiMode;

@Singleton
public class DefaultUiModeProvider implements Provider<UiMode> {
private final TendiwaInputProcessorFactory factory;
private final ActionsAdder mappings;
private final InputToActionMapper mapper;

@Inject
DefaultUiModeProvider(
	TendiwaInputProcessorFactory factory,
	@Named("default") ActionsAdder mappings,
	@Named("default") InputToActionMapper mapper
) {
	this.factory = factory;
	this.mappings = mappings;
	this.mapper = mapper;
}

@Override
public UiMode get() {
	TendiwaInputProcessor uiMode = factory.create(mapper);
	uiMode.addMappings(mappings);
	return uiMode;
}
}
