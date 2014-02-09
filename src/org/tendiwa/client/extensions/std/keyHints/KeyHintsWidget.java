package org.tendiwa.client.extensions.std.keyHints;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.google.inject.Inject;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.client.Languages;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.input.Mapping;
import org.tendiwa.client.ui.uiModes.UiModeManager;

public class KeyHintsWidget extends TendiwaWidget {
private final Label.LabelStyle labelStyle;
private final GameScreenViewport viewport;
private final UiModeManager uiModeManager;

@Inject
KeyHintsWidget(
	FontRegistry fontRegistry,
	ColorFillFactory colorFillFactory,
	GameScreenViewport viewport,
	final UiModeManager uiModeManager
) {
	this.viewport = viewport;
	this.uiModeManager = uiModeManager;
	this.labelStyle = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
}

public void update() {
	clearChildren();
	for (Mapping mapping : uiModeManager.getCurrentMode().getMapper()) {
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
