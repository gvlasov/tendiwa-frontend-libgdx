package org.tendiwa.client;

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
private static TendiwaUiStage INSTANCE;
private static UiInventory inventory;
private final GameScreen gameScreen;
private Skin skin;

private TendiwaUiStage(GameScreen gameScreen, SpriteBatch batch) {
	super(gameScreen.windowWidth, gameScreen.windowHeight, true, batch);
	this.gameScreen = gameScreen;
}

public static TendiwaUiStage getInstance() {
	if (INSTANCE == null) {
		throw new UnsupportedOperationException("TendiwaUiStage has not yet been initiated");
	}
	return INSTANCE;
}

public static TendiwaUiStage init(GameScreen gameScreen) {
	if (INSTANCE != null) {
		throw new UnsupportedOperationException("TendiwaUiStage has already been initiated");
	}
	SpriteBatch batch = new SpriteBatch();
	OrthographicCamera camera = new OrthographicCamera(gameScreen.windowWidth, gameScreen.windowHeight);
	camera.setToOrtho(false, gameScreen.windowWidth, gameScreen.windowHeight);
//	batch.setProjectionMatrix(camera.combined);
	INSTANCE = new TendiwaUiStage(gameScreen, batch);
	INSTANCE.setCamera(camera);
	INSTANCE.initializeActors();
	return INSTANCE;
}

static Image createImage(Color color) {
	System.out.println(INSTANCE);
	Image image = new Image(INSTANCE.skin.newDrawable("white", color));
	return image;
}

public static UiInventory getInventory() {
	return inventory;
}

private void initializeActors() {
	initializeStyles();

	Table table = createTable();
	table.setSize(gameScreen.windowWidth, gameScreen.windowHeight);
	this.addActor(table);
	inventory = new UiInventory();
	table.add(inventory).expand().right().bottom();
	table.layout();
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
