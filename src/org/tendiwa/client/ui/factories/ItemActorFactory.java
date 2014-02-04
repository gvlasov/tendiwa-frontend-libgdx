package org.tendiwa.client.ui.factories;

import org.tendiwa.client.ItemActor;
import org.tendiwa.core.Item;
import org.tendiwa.core.RenderPlane;

public interface ItemActorFactory {
public ItemActor create(int x, int y, Item item, RenderPlane renderPlane);
}
