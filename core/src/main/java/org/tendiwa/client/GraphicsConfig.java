package org.tendiwa.client;

public class GraphicsConfig {
public boolean fpsCounter = true;
public boolean animationsEnabled = true;
public boolean animateLiquidFloor = true;
public boolean limitFps = false;
public boolean vSync = false;

public void toggleAnimations() {
	animationsEnabled = !animationsEnabled;
}

public void toggleStatusBar() {
	fpsCounter = !fpsCounter;
}
}
