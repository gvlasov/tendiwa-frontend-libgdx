package org.tendiwa.client.ui.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.ItemActor;
import org.tendiwa.core.Item;
import org.tendiwa.core.RenderPlane;

public interface ItemActorFactory {
public ItemActor create(
	@Assisted("x") int x,
	@Assisted("y") int y,
	Item item,
	RenderPlane renderPlane
);
}
