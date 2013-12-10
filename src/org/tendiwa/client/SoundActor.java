package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import tendiwa.core.SoundType;

public class SoundActor extends Actor {
private static final Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.RED);

public SoundActor(SoundType soundType) {
	Label label = new Label(soundType.getName(), style);
	label.setOrigin(0.5f, 0.5f);
}
}
