package org.tendiwa.client.extensions.std.itemSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.inject.Inject;
import org.tendiwa.client.EntityFilter;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.ui.widgets.UiItemSelectionTable;
import org.tendiwa.core.Item;

public class ItemSelectionScreen extends ScreenAdapter {
private final Stage stage;
private final UiItemSelectionTable table;

@Inject
public ItemSelectionScreen(LwjglApplicationConfiguration config, UiItemSelectionTable table) {
	stage = new Stage(config.width, config.height, true);
//	OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//	camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//	stage.setCamera(camera);
	this.table = table;
	table.setTransform(true);
	table.setFillParent(true);
	stage.addActor(table);
}

@Override
public void render(float delta) {
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	stage.draw();
}

@Override
public void resize(int width, int height) {
	stage.setViewport(width, height, true);
}

public void updateTable(ItemToKeyMapper<Item> mapper, EntityFilter<Item> filter) {
	table.update(mapper, filter);
}
}
