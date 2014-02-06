package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Inject;

public class TendiwaApplication extends LwjglApplication {
@Inject
public TendiwaApplication(Game screenSwitcher, LwjglApplicationConfiguration config) {
	super(screenSwitcher, config);
}

}
