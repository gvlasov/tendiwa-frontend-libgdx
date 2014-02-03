package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import org.tendiwa.core.EntityPlacer;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.World;
import org.tendiwa.groovy.Registry;

public class WorldMapScreen implements Screen {
private final World world;
private OrthographicCamera camera;
private SpriteBatch batch;

@Inject
WorldMapScreen(World world) {
	this.world = world;
	camera = new OrthographicCamera();
	camera.setToOrtho(true, Tendiwa.getWorldWidth(), Tendiwa.getWorldHeight());

	batch = new SpriteBatch();
	setRenderOnce();
}

private void setRenderOnce() {
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
	HorizontalPlane plane = world.getDefaultPlane();
	int width = world.getWidth();
	int height = world.getHeight();
	Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

	for (int x = 0; x < width; x++) {
		for (int y = 0; y < height; y++) {
			int color;
			if (plane.hasCharacter(x, y)) {
				color = Color.rgba8888(0.2f, 1.0f, 1.0f, 1);
			} else if (plane.hasAnyItems(x, y)) {
				color = Color.rgba8888(0.2f, 0.4f, 1.0f, 1);
			} else if (EntityPlacer.containedIn(plane, Registry.wallTypes.get("grey_stone_wall"), x, y)) {
				color = Color.rgba8888(0.2f, 0.2f, 0.2f, 1);
			} else if (EntityPlacer.containedIn(plane, Registry.floorTypes.get("water"), x, y)) {
				color = Color.rgba8888(0.2f, 0.3f, 0.93f, 1);
			} else {
				color = Color.rgba8888(0.2f, 0.7f, 0.3f, 1);
			}
			pixmap.drawPixel(x, y, color);
		}
	}
	batch.draw(new Texture(pixmap), 0, 0);
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
	setRenderOnce();
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
