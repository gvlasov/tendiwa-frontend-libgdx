package org.tendiwa.client.ui.uiModes;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.ui.input.ActionsAdder;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.client.ui.input.TendiwaInputProcessor;
import org.tendiwa.client.ui.input.TendiwaInputProcessorFactory;

public class UiMode {
final TendiwaInputProcessor inputProcessor;
private final UiModeManager manager;

@Inject
UiMode(
	TendiwaInputProcessorFactory factory,
	InputToActionMapper mapper,
	UiModeManager manager,
	@Assisted ActionsAdder adder
) {
	adder.addTo(mapper);
	this.inputProcessor = factory.create(mapper);
	this.manager = manager;
}

protected void abort() {
	if (!manager.isModeLast(this)) {
		throw new RuntimeException("Can't abort mode that is not current");
	}
	manager.popMode();
}

public TendiwaInputProcessor getInputProcessor() {
	return inputProcessor;
}
}
