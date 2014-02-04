package org.tendiwa.client.ui.factories;

import org.tendiwa.client.ProjectileActor;
import org.tendiwa.core.Projectile;
import org.tendiwa.core.RenderPlane;

public interface ProjectileActorFactory {
public ProjectileActor create(Projectile projectile, int fromX, int fromY, int toX, int toY, RenderPlane renderPlane);
}
