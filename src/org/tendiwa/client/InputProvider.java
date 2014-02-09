package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.google.inject.Exposed;
import com.google.inject.Provider;

public class InputProvider implements Provider<Input> {
@Override
public Input get() {
	return Gdx.input;
}
}
