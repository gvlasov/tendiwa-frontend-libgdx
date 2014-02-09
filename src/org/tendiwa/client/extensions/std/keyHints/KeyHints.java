package org.tendiwa.client.extensions.std.keyHints;

import com.badlogic.gdx.Input;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.UiPlugin;
import org.tendiwa.client.ui.input.*;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;

public class KeyHints implements UiPlugin {
private final UiMode uiMode;
private final KeyHintsWidget widget;
private final UiModeManager uiModeManager;
private final DefaultKeyMappings defaultKeyMappings;

public KeyHints(
	KeyHintsWidget widget,
	UiModeManager uiModeManager,
	@Named("key_hints") UiMode uiMode,
	DefaultKeyMappings defaultKeyMappings
) {
	this.widget = widget;
	this.uiModeManager = uiModeManager;
	this.uiMode = uiMode;
	this.defaultKeyMappings = defaultKeyMappings;
}

@Override
public void configure() {
	uiMode.addMappings(new ActionsAdder() {
		@Override
		public void addTo(InputToActionMapper mapper) {
			mapper.putAction(Input.Keys.ESCAPE, new KeyboardAction("actions.abort_ui_mode") {
				@Override
				public void act() {
					KeyHints.this.uiModeManager.popMode();
					KeyHints.this.widget.setVisible(false);
				}
			});
		}
	});
	defaultKeyMappings.addMapping(
		new KeyboardInputMapping(Modifiers.shift + Input.Keys.SLASH),
		new KeyboardAction("ui.actions.key_hints.show_hints") {
			@Override
			public void act() {
				widget.update();
				widget.setVisible(true);
				uiModeManager.pushMode(uiMode);
			}
		});
}
}
