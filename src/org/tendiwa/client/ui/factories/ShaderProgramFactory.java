package org.tendiwa.client.ui.factories;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.inject.Inject;

public class ShaderProgramFactory {
private final ShaderProgram defaultShader;

@Inject
public ShaderProgramFactory() {
	ShaderProgram.pedantic = false;
	this.defaultShader = SpriteBatch.createDefaultShader();
}

public ShaderProgram create(FileHandle file) {
	ShaderProgram shader = new ShaderProgram(defaultShader.getVertexShaderSource(), file.readString());
	if (!shader.isCompiled()) {
		throw new RuntimeException(shader.getLog());
	}
	return shader;
}
}
