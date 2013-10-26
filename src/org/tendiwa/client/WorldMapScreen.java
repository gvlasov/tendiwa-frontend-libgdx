package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tendiwa.core.Cell;
import tendiwa.resources.FloorTypes;
import tendiwa.resources.ObjectTypes;

public class WorldMapScreen implements Screen {
private final TendiwaGame game;
private final Texture bucket = new Texture(Gdx.files.internal("assets/bucket.png"));
private OrthographicCamera camera;
private SpriteBatch batch;

WorldMapScreen(final TendiwaGame game) {
	this.game = game;
	camera = new OrthographicCamera();
	camera.setToOrtho(false, TendiwaGame.WIDTH, TendiwaGame.HEIGHT);

	batch = new SpriteBatch();
	Gdx.graphics.setContinuousRendering(false);
	Gdx.graphics.requestRendering();
}

@Override
public void render(float delta) {
	Gdx.gl.glClearColor(0.0f, 0.0f, 0.2f, 1);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	camera.update();
	batch.setProjectionMatrix(camera.combined);
	batch.begin();
	Cell[][] cellContents = game.world.getCellContents();
	int width = cellContents.length;
	int height = cellContents[0].length;
	int width1 = nearestPowerOf2(width);
	int height1 = nearestPowerOf2(height);
	Pixmap pixmap = new Pixmap(width1, height1, Pixmap.Format.RGBA8888);

	for (int x = 0; x < width; x++) {
		for (int y = 0; y < height; y++) {
			Cell cell = cellContents[x][y];
			int color;
			if (ObjectTypes.wall_grey_stone.containedIn(cell)) {
				color = Color.rgba8888(0.2f, 0.2f, 0.2f, 1);
			} else if (FloorTypes.water.containedIn(cell)) {
				color = Color.rgba8888(0.2f, 0.3f, 0.93f, 1);
			} else {
				color = Color.rgba8888(0.2f, 0.7f, 0.3f, 1);
			}
			pixmap.drawPixel(x, y, color);
		}
	}
	batch.draw(new Texture(pixmap), 0, -424.0f);
	batch.end();

}

private int nearestPowerOf2(int number) {
	return (int) Math.pow(2, Math.ceil(Math.log(number) / Math.log(2)));
}

@Override
public void resize(int width, int height) {

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
