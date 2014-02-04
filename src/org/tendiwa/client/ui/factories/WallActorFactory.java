package org.tendiwa.client.ui.factories;

import org.tendiwa.client.WallActor;
import org.tendiwa.core.RenderPlane;
import org.tendiwa.core.WallType;

public interface WallActorFactory {
public WallActor create(int x, int y, WallType gameObject, RenderPlane renderPlane);
}
