package org.tendiwa.client.ui.factories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.SoundActor;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.core.SoundType;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

@Singleton
public class SoundActorFactory {
private final FontRegistry fontRegistry;
private final ColorFillFactory colorFillFactory;

@Inject
SoundActorFactory(FontRegistry fontRegistry, ColorFillFactory colorFillFactory) {
	this.fontRegistry = fontRegistry;
	this.colorFillFactory = colorFillFactory;
}

public SoundActor create(SoundType type, int x, int y) {
	final SoundActor actor = new SoundActor(type, fontRegistry, colorFillFactory);
	actor.setPosition(
		x * GameScreen.TILE_SIZE - SoundActor.width / 2 + GameScreen.TILE_SIZE / 2,
		y * GameScreen.TILE_SIZE - SoundActor.width / 2 + GameScreen.TILE_SIZE / 2
	);
	return actor;
}
}
