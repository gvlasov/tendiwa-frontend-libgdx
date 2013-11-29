package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class TendiwaUiStage extends Stage {

private final GameScreen gameScreen;
private Skin skin;

public TendiwaUiStage(GameScreen gameScreen) {
	super(gameScreen.windowWidth, gameScreen.windowHeight);
	this.gameScreen = gameScreen;
	initializeActors();
}

private void initializeActors() {
	initializeStyles();

	Table table = createTable();
	table.setSize(gameScreen.windowWidth, gameScreen.windowHeight);
	TextButton button = createButton();
	this.addActor(table);
	table.add(createImage()).size(gameScreen.windowWidth, 400).colspan(2);
	table.row();
	table.add(button).size(100, 200).left().center();

	table.add(createImage()).size(64).bottom();
	table.layout();
}

private Table createTable() {
	Table table = new Table();
	table.setFillParent(true);
	return table;
}

private Image createImage() {
	return new Image(skin.newDrawable("white", Color.RED));
}

private TextButton createButton() {

	final TextButton button = new TextButton("Click me", skin);

	button.addListener(new ChangeListener() {
		public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			System.out.println("Clicked! Is checked: " + button.isChecked());
			button.setText("Good job!");
		}
	});
	return button;
}

private void initializeStyles() {
	skin = new Skin();

	Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
	pixmap.setColor(1, 1, 1, 1);
	pixmap.fill();
	skin.add("white", new Texture(pixmap));
	skin.add("default", new BitmapFont());

	TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
	textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
	textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
	textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
	textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
	textButtonStyle.font = skin.getFont("default");
	skin.add("default", textButtonStyle);
}

}
