package org.tendiwa.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BookFun implements ApplicationListener {
SpriteBatch batch;
OrthographicCamera camera;
TextureAtlas atlas;
Sprite sprite;
private float rotation;
private float degreesPerSecond = 180;

public BookFun() {
	atlas = new TextureAtlas(Gdx.files.internal("pack/package.atlas"));
	camera = new OrthographicCamera(1024, 768);
	camera.setToOrtho(true, 1024, 768);

	sprite = atlas.createSprite("water");
	batch = new SpriteBatch();
	batch.setProjectionMatrix(camera.combined);

}

@Override
public void render() {

	Gdx.gl.glClearColor(1, 1, 1, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	batch.begin();
	rotation = (rotation + Gdx.graphics.getDeltaTime() * degreesPerSecond) % 360;
	sprite.setRotation(rotation);
	batch.draw(sprite, 100, 100);
	batch.end();
}

@Override
public void create() {

}

@Override
public void resize(int width, int height) {

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
