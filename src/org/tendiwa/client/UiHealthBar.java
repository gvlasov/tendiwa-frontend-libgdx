package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.tablelayout.Cell;
import tendiwa.core.Tendiwa;

public class UiHealthBar extends TendiwaWidget {
private final int width;
private final Cell greenZone;
private final Cell redZone;
private final Cell blackZone;
private static UiHealthBar INSTANCE;

private UiHealthBar(int width, int height) {
	super();
	this.width = width;
	setWidth(width);
	setHeight(height);
	greenZone = add(TendiwaUiStage.createImage(Color.GREEN)).width(width/3).height(height);
	redZone = add(TendiwaUiStage.createImage(Color.RED)).width(width/3).height(height);
	blackZone = add(TendiwaUiStage.createImage(Color.BLACK)).width(width/3).height(height);
}

public static UiHealthBar getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new UiHealthBar(200, 16);
	}
	return INSTANCE;
}

@Override
public void update() {
	int hp = Tendiwa.getPlayerCharacter().getHP();
	int maxHp = Tendiwa.getPlayerCharacter().getMaxHP();
	int greenZoneWidth = Math.max(width * hp / maxHp, 0);
	greenZone.width(greenZoneWidth);
	blackZone.width(width-greenZoneWidth);
	redZone.width(0);
	invalidate();
}
}
