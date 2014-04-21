package org.tendiwa.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.ui.cellSelection.CellSelectionActor;

public class TendiwaUiStage extends Stage {
private final Skin skin;
private final WidgetsPlacer widgetsPlacer;
private final Table table;

@Inject
TendiwaUiStage(
	WidgetsPlacer widgetsPlacer,
	@Named("ui_base") Table uiBaseTable,
	@Named("ui_base") Skin skin
) {
	super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), new SpriteBatch());
	this.widgetsPlacer = widgetsPlacer;
	this.skin = skin;
	this.table = uiBaseTable;
}

public void buildUi() {
	OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	getViewport().setCamera(camera);

	addActor(table);
	widgetsPlacer.placeWidgets(this, table);
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
}
