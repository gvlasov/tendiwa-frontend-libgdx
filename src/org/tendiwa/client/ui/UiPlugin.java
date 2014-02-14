package org.tendiwa.client.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface UiPlugin {
public void configure();
public Actor getWidget();
}
