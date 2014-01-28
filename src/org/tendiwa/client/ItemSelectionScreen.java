package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.tendiwa.core.Item;

public class ItemSelectionScreen implements Screen, EntityProvider<Item> {
private final ItemSelectionScreenOnComplete onComplete;
private final Stage stage;
private final UiItemSelectionTable table;
private final EntityProvider<Item> entityProvider = new ItemSelectionScreenEntityProvider();
private ItemSelectionInputProcessor<Item> inputProcessor = null;

public ItemSelectionScreen(ItemSelectionScreenOnComplete onComplete, EntityProvider<Item> entityProvider) {
	this.onComplete = onComplete;
	this.entityProvider = entityProvider;
	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
//	OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//	camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//	stage.setCamera(camera);
	this.table = new UiItemSelectionTable();
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

@Override
public void show() {
}

@Override
public void hide() {

}

@Override
public void pause() {

}

@Override
public void resume() {

}

@Override
public void dispose() {

}

}
