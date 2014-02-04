package org.tendiwa.client.ui.factories;

import org.tendiwa.client.BorderObjectActor;
import org.tendiwa.core.Border;
import org.tendiwa.core.BorderObject;
import org.tendiwa.core.RenderPlane;

public interface BorderObjectActorFactory {
public BorderObjectActor create(Border border, BorderObject borderObject, RenderPlane renderPlaner);
}
