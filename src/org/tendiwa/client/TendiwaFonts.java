package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TendiwaFonts {
/**
 * A non-flipped font for writing within UI scene (which has y axis up).
 */
public final static BitmapFont default14NonFlipped = new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf"))
	.generateFont(14, "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_.,-'\"", false);
protected static final BitmapFont default8NonFlipped = new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf"))
	.generateFont(8, "1234567890qwertyuiopasdfghjklzxcvbnm,.-QWERTYUIOPASDFGHJKLZXCVBNM", false);
}
