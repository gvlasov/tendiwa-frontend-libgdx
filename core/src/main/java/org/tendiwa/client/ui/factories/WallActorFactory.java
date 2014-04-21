package org.tendiwa.client.ui.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.WallActor;
import org.tendiwa.core.clients.RenderPlane;
import org.tendiwa.core.WallType;

public interface WallActorFactory {
public WallActor create(
	@Assisted("x") int x,
	@Assisted("y") int y,
	WallType gameObject,
	RenderPlane renderPlane
);
}
