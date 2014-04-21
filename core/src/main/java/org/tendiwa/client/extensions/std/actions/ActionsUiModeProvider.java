package org.tendiwa.client.extensions.std.actions;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.tendiwa.client.EntitySelectionInputProcessor;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.CharacterAbility;

public class ActionsUiModeProvider implements Provider<UiMode> {
private final ItemToKeyMapper<CharacterAbility> mapper;
private final ActionSelectionListener onActionSelected;
private final UiModeManager uiModeManager;
private final ActionsWidget widget;

@Inject
ActionsUiModeProvider(
	@Named("actions_widget") ItemToKeyMapper<CharacterAbility> mapper,
	ActionSelectionListener onActionSelected,
	UiModeManager uiModeManager,
	ActionsWidget widget
) {

	this.mapper = mapper;
	this.onActionSelected = onActionSelected;
	this.uiModeManager = uiModeManager;
	this.widget = widget;
}

@Override
public UiMode get() {
	return new EntitySelectionInputProcessor<>(mapper, onActionSelected, new Runnable() {
		@Override
		public void run() {
			widget.setVisible(false);
			uiModeManager.popMode();
		}
	});
}
}
