package org.tendiwa.client.ui.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.ProjectileActor;
import org.tendiwa.core.Projectile;
import org.tendiwa.core.RenderPlane;

public interface ProjectileActorFactory {
public ProjectileActor create(
	Projectile projectile,
	@Assisted("fromX") int fromX,
	@Assisted("fromY") int fromY,
	@Assisted("toX") int toX,
	@Assisted("toY") int toY,
	RenderPlane renderPlane
);
}
