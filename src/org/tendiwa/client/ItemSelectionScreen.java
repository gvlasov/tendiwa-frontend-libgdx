package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import tendiwa.core.Item;

public class ItemSelectionScreen implements Screen, EntityProvider<Item> {
//private final UiItemSelectionTable table;
private final Stage stage;
private final UiItemSelectionTable table;
private ItemToKeyMapper<Item> mapper;
private Iterable<Item> items;
private ItemSelectionInputProcessor<Item> inputProcessor = new ItemSelectionInputProcessor<>();

public ItemSelectionScreen() {
	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
//	OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//	camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//	stage.setCamera(camera);
	this.table = new UiItemSelectionTable();
	table.setTransform(true);
	table.setFillParent(true);
	stage.addActor(table);
	inputProcessor.setPersistentListener(new EntitySelectionListener<Item>() {
		@Override
		public void execute(Item entity) {
			TendiwaGame.switchToGameScreen();
		}
	});
}

@Override
public void render(float delta) {
	assert items != null;
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	stage.draw();
}

public void setItemToKeyMapper(ItemToKeyMapper<Item> mappings) {
	this.mapper = mappings;
}

public void setItemsCollection(Iterable<Item> items) {
	this.items = items;
}

@Override
public void resize(int width, int height) {
	stage.setViewport(width, height, true);
}

@Override
public void show() {
	mapper.update(items);
	table.update(mapper);
	inputProcessor.setMapper(mapper);
	Gdx.input.setInputProcessor(inputProcessor);
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

@Override
public void startEntitySelection(EntitySelectionListener<Item> listener) {
	TendiwaGame.switchToItemSelectionScreen();
	inputProcessor.setListener(listener);

}
}
