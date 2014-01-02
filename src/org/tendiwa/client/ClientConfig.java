package org.tendiwa.client;

public class ClientConfig {
public boolean fpsCounter = true;
public boolean animationsEnabled = false;
public boolean animateLiquidFloor = true;
public boolean limitFps = false;

public void toggleAnimations() {
	animationsEnabled = !animationsEnabled;
}

public void toggleStatusBar() {
	fpsCounter = !fpsCounter;
}
}
