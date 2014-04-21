package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.core.SoundType;

public class SoundActor extends Table {
public static final int width = 100;

public SoundActor(SoundType soundType, FontRegistry fontRegistry, ColorFillFactory colorFillFactory) {
	assert soundType != null;
	Label label = new Label(soundType.getName(), new Label.LabelStyle(
		fontRegistry.obtain(14, true),
		Color.RED
	));
	add(label).expand().center();
	setOrigin(0.5f, 0.5f);
	setSize(SoundActor.width, SoundActor.width);
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 0.4f)).getDrawable());
}

}
