package org.tendiwa.client.ui.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.ObjectActor;
import org.tendiwa.core.GameObject;
import org.tendiwa.core.RenderPlane;

public interface ObjectActorFactory {
public ObjectActor create(
	@Assisted("x") int x,
	@Assisted("y") int y,
	GameObject gameObject,
	RenderPlane renderPlane
);
}
