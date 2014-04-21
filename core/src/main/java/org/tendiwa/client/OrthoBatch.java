package org.tendiwa.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OrthoBatch extends SpriteBatch {
public OrthoBatch(int width, int height) {
	OrthographicCamera camera = new OrthographicCamera(width, height);
	camera.setToOrtho(true, width, height);
	setProjectionMatrix(camera.combined);
}
}
