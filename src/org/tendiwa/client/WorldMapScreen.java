package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.Tendiwa;
import tendiwa.core.World;
import tendiwa.resources.ObjectTypes;
import tendiwa.resources.TerrainTypes;

public class WorldMapScreen implements Screen {
private final TendiwaGame game;
private OrthographicCamera camera;
private SpriteBatch batch;

WorldMapScreen(final TendiwaGame game) {
	this.game = game;
	camera = new OrthographicCamera();
	camera.setToOrtho(false, Tendiwa.getWorld().getWidth(), Tendiwa.getWorld().getHeight());

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
	World world = Tendiwa.getWorld();
	HorizontalPlane plane = world.getDefaultPlane();
	int width = world.getWidth();
	int height = world.getHeight();
	int width1 = nearestPowerOf2(width);
	int height1 = nearestPowerOf2(height);
	Pixmap pixmap = new Pixmap(width1, height1, Pixmap.Format.RGBA8888);

	for (int x = 0; x < width; x++) {
		for (int y = 0; y < height; y++) {
			int color;
			if (TerrainTypes.wall_grey_stone.containedIn(plane, x, y)) {
				color = Color.rgba8888(0.2f, 0.2f, 0.2f, 1);
			} else if (TerrainTypes.water.containedIn(plane, x, y)) {
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
