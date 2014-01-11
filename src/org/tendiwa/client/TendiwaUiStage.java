package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class TendiwaUiStage extends Stage {
private static UiInventory inventory;
private static Skin skin;
private UiQuiver uiQuiver;

TendiwaUiStage() {
	super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, new SpriteBatch());
	OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	setCamera(camera);
	initializeStyles();
	initializeActors();
}

static Image createImage(Color color) {
	return new Image(skin.newDrawable("white", color));
}

public static UiInventory getInventory() {
	return inventory;
}

private static void initializeStyles() {
	// Lazily
	if (skin == null) {
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

private void initializeActors() {
	Table table = createTable();
	table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	this.addActor(table);
	UiActions actions = UiActions.getInstance();
	UiSpells spells = UiSpells.getInstance();
	inventory = new UiInventory();
	UiLog log = UiLog.getInstance();
//	table.add(log).width(400).height(200).expand().pad(5).left().top().colspan(2);
	table.add(UiHealthBar.getInstance()).expand().right().top().pad(5).colspan(1);
	table.row();
	uiQuiver = new UiQuiver();
	table.add(uiQuiver).expand().right().bottom().pad(5).colspan(3);
	table.row();
	table.add(actions).left().bottom().pad(5).size(200, 100);
	table.add(spells).pad(5).size(200, 100);
	table.add(inventory).right().bottom().pad(5).size(200, 100);
	inventory.update();
	actions.update();
//	log.update();
	UiHealthBar.getInstance().update();

	this.addActor(UiKeyHints.getInstance());

	UiKeyHints.getInstance().setVisible(false);

	actions.setVisible(false);
	spells.setVisible(false);
}

private Table createTable() {
	Table table = new Table();
	table.setFillParent(true);
	return table;
}

private TextButton createButton() {
	final TextButton button = new TextButton("Click me", skin);
	button.addListener(new ChangeListener() {
		public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			button.setText("Good job!");
		}
	});
	return button;
}

public UiQuiver getQuiver() {
	return uiQuiver;
}
}
