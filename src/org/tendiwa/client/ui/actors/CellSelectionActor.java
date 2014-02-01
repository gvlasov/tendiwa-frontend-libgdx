package org.tendiwa.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class CellSelectionActor extends Actor {
protected int worldY;
protected int worldX;

protected CellSelectionActor() {
}

public void deactivate() {
	setVisible(false);
}

public void activate() {
	setVisible(true);
}

public void setWorldCoordinates(int worldX, int worldY) {
	this.worldX = worldX;
	this.worldY = worldY;
}
}