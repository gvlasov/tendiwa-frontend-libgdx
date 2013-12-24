package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class UiKeyHints extends TendiwaWidget {
private static final Label.LabelStyle labelStyle = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.WHITE);
private static UiKeyHints INSTANCE;
private InputProcessor inputProcessor= new InputProcessor() {
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE) {
			Gdx.input.setInputProcessor(TendiwaGame.getGameScreen().getInputProcessor());
			UiKeyHints.this.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
};

private UiKeyHints() {
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
}

public static UiKeyHints getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new UiKeyHints();
	}
	return INSTANCE;
}

@Override
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

public InputProcessor getInputProcessor() {
	return inputProcessor;
}
}
