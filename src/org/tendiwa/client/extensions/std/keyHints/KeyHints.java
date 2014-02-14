package org.tendiwa.client.extensions.std.keyHints;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.UiPlugin;
import org.tendiwa.client.ui.input.*;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;

@Singleton
public class KeyHints implements UiPlugin {
private final KeyHintsWidget widget;
private final UiModeManager uiModeManager;
private final TendiwaInputProcessorFactory factory;
private final DefaultKeyMappings defaultKeyMappings;
private final InputToActionMapper mapper;

@Inject
public KeyHints(
	KeyHintsWidget widget,
	UiModeManager uiModeManager,
	DefaultKeyMappings defaultKeyMappings,
    TendiwaInputProcessorFactory factory,
    InputToActionMapper mapper
) {
	this.widget = widget;
	this.uiModeManager = uiModeManager;
	this.factory = factory;
	this.defaultKeyMappings = defaultKeyMappings;
	this.mapper = mapper;
}

@Override
public void configure() {
	final TendiwaInputProcessor keyHintsUiMode = factory.create(mapper);
	keyHintsUiMode.addMappings(new ActionsAdder() {
		@Override
		public void addTo(InputToActionMapper mapper) {
			mapper.putAction(Input.Keys.ESCAPE, new KeyboardAction("actions.abort_ui_mode") {
				@Override
				public void act() {
					uiModeManager.popMode();
					widget.setVisible(false);
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
				uiModeManager.pushMode(keyHintsUiMode);
			}
		});
}

@Override
public Actor getWidget() {
	return widget;
}
}
