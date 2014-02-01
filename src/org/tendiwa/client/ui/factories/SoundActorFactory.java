package org.tendiwa.client.ui.factories;

import org.tendiwa.client.EventProcessor;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.SoundActor;
import org.tendiwa.client.TendiwaStage;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.core.SoundType;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class SoundActorFactory {
private final FontRegistry fontRegistry;
private final ColorFillFactory colorFillFactory;
private final EventProcessor eventProcessor;
private final TendiwaStage stage;

SoundActorFactory(FontRegistry fontRegistry, ColorFillFactory colorFillFactory, EventProcessor eventProcessor, TendiwaStage stage) {
	this.fontRegistry = fontRegistry;

	this.colorFillFactory = colorFillFactory;
	this.eventProcessor = eventProcessor;
	this.stage = stage;
}

public SoundActor create(SoundType type, int x, int y) {
	final SoundActor actor = new SoundActor(type, fontRegistry, colorFillFactory);
	actor.setPosition(
		x * GameScreen.TILE_SIZE - SoundActor.width / 2 + GameScreen.TILE_SIZE / 2,
		y * GameScreen.TILE_SIZE - SoundActor.width / 2 + GameScreen.TILE_SIZE / 2
	);
	actor.addAction(sequence(rotateBy(90, 0.3f), run(new Runnable() {
		@Override
		public void run() {
			stage.getRoot().removeActor(actor);
			eventProcessor.signalEventProcessingDone();
		}
	})));
	return actor;
}
}
