package org.tendiwa.client.ui.factories;

import com.bitfire.postprocessing.PostProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.FloorLayer;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.client.WallActor;
import org.tendiwa.core.Character;
import org.tendiwa.core.RenderPlane;
import org.tendiwa.core.RenderWorld;
import org.tendiwa.core.WallType;

@Singleton
public class WallActorFactory {
private final GameScreenViewport viewport;
private final RenderWorld renderWorld;
private final PostProcessor postProcessor;
private final FloorLayer floorLayer;
private final Character player;

@Inject
WallActorFactory(GameScreenViewport viewport, RenderWorld renderWorld, PostProcessor postProcessor, FloorLayer floorLayer, Character player) {
	this.viewport = viewport;
	this.renderWorld = renderWorld;
	this.postProcessor = postProcessor;
	this.floorLayer = floorLayer;
	this.player = player;
}

public WallActor create(int x, int y, WallType gameObject, RenderPlane renderPlane) {
	return new WallActor(postProcessor, renderWorld, floorLayer, player, viewport, renderPlane, x, y, gameObject);
}
}
