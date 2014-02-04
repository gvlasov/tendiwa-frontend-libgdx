package org.tendiwa.client.ui.factories;

import org.tendiwa.client.ObjectActor;
import org.tendiwa.core.GameObject;
import org.tendiwa.core.RenderPlane;

public interface ObjectActorFactory {
public ObjectActor create(int x, int y, GameObject gameObject, RenderPlane renderPlane);
}
