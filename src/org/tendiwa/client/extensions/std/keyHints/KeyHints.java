package org.tendiwa.client.extensions.std.keyHints;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.client.Languages;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.input.*;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeFactory;
import org.tendiwa.client.ui.uiModes.UiModeManager;

public class KeyHints extends TendiwaWidget {
private final Label.LabelStyle labelStyle;
private final GameScreen gameScreen;
private final UiMode inputProcessor;
private final GameScreenViewport viewport;
private final UiModeManager uiModeManager;

@Inject
KeyHints(
	InputToActionMapper actionMapper,
	GameScreen gameScreen,
	FontRegistry fontRegistry,
	ColorFillFactory colorFillFactory,
	final Input gdxInput,
	GameScreenViewport viewport,
	@Named("default") final InputProcessor defaultInputProcessor,
    UiModeFactory uiModeFactory,
    final UiModeManager uiModeManager
) {
	this.viewport = viewport;
	this.uiModeManager = uiModeManager;
	this.labelStyle = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	this.gameScreen = gameScreen;
	this.inputProcessor = uiModeFactory.create(new ActionsAdder() {
		@Override
		public void addTo(InputToActionMapper mapper) {
			mapper.putAction(Input.Keys.ESCAPE, new KeyboardAction("actions.abort_ui_mode") {
				@Override
				public void act() {
					uiModeManager.popMode();
					setVisible(false);
				}
			});
		}
	});
	actionMapper.putAction(InputToActionMapper.shift + Input.Keys.SLASH, new KeyboardAction("action.key_hints") {
		@Override
		public void act() {
			update();
			setVisible(true);
			uiModeManager.pushMode(inputProcessor);
		}
	});
}

public void update() {
	clearChildren();
	for (Mapping mapping : uiModeManager.getCurrentMode().getInputProcessor().getMapper()) {
		String combinationText = mapping.getCombination().toString();
		String actionNameLocalized = Languages.getText(
			mapping.getAction().getLocalizationId()
		);
		add(createKey(combinationText)).pad(5);
		add(createActionName(actionNameLocalized)).pad(5).fillX().align(Align.left);
		row();
	}
	pack();
	setPosition(
		viewport.getWindowWidthPixels() / 2 - getWidth() / 2,
		viewport.getWindowHeightPixels() / 2 - getHeight() / 2
	);
}

private Actor createKey(String combinationText) {
	return new Label(combinationText, labelStyle);
}

private Actor createActionName(String actionNameLocalized) {
	return new Label(actionNameLocalized, labelStyle);
}

}
