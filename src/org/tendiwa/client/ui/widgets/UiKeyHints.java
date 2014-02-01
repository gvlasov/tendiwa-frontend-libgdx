package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.google.inject.Inject;
import org.tendiwa.client.*;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;

public class UiKeyHints extends TendiwaWidget {
private final Label.LabelStyle labelStyle;
private final GameScreen gameScreen;
private final InputProcessor inputProcessor;

@Inject
UiKeyHints(GameScreen gameScreen, FontRegistry fontRegistry, ColorFillFactory colorFillFactory) {
	this.labelStyle  = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	this.gameScreen = gameScreen;
	this.inputProcessor = new UiKeyHintsInputProcessor(new Runnable() {
		@Override
		public void run() {
			Gdx.input.setInputProcessor(UiKeyHints.this.gameScreen.getInputProcessor());
			setVisible(false);
		}
	});
}

public void update() {
	clearChildren();
	for (Mapping mapping : TendiwaInputProcessor.getCurrentMappings()) {
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
		Gdx.graphics.getWidth() / 2 - getWidth() / 2,
		Gdx.graphics.getHeight() / 2 - getHeight() / 2
	);
}

private Actor createKey(String combinationText) {
	return new Label(combinationText, labelStyle);
}

private Actor createActionName(String actionNameLocalized) {
	return new Label(actionNameLocalized, labelStyle);
}

@Override
public void onShow() {
	Gdx.input.setInputProcessor(inputProcessor);
}
}
