package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import tendiwa.core.SoundType;

public class SoundActor extends Table {
private static final Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14Flipped, Color.RED);
static int width = 100;

public SoundActor(SoundType soundType) {
	assert soundType != null;
	Label label = new Label(soundType.getName(), style);
	add(label).expand().center();
	setOrigin(0.5f, 0.5f);
	setSize(SoundActor.width, SoundActor.width);
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 0.4f)).getDrawable());
}

}
