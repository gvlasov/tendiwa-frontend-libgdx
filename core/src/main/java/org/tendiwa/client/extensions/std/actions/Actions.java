package org.tendiwa.client.extensions.std.actions;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.UiPlugin;
import org.tendiwa.client.ui.input.DefaultKeyMappings;
import org.tendiwa.client.ui.input.KeyboardAction;
import org.tendiwa.client.ui.input.KeyboardInputMapping;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;

public class Actions implements UiPlugin {
private final ActionsWidget widget;
private final DefaultKeyMappings defaultKeyMappings;
private final UiMode actionsUiMode;
private final UiModeManager uiModeManager;

@Inject
Actions(
	ActionsWidget widget,
	DefaultKeyMappings defaultKeyMappings,
	@Named("actions") UiMode actionsUiMode,
	UiModeManager uiModeManager
) {
	this.widget = widget;
	this.defaultKeyMappings = defaultKeyMappings;
	this.actionsUiMode = actionsUiMode;
	this.uiModeManager = uiModeManager;
}

@Override
public void configure() {
	defaultKeyMappings.addMapping(
		new KeyboardInputMapping(Input.Keys.A),
		new KeyboardAction("action.actionsMenu") {
			@Override
			public void act() {
				widget.setVisible(true);
				widget.update();
				uiModeManager.pushMode(actionsUiMode);
			}
		}
	);
}

@Override
public Actor getWidget() {
	return widget;
}
}
