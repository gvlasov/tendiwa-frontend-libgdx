package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.tablelayout.Cell;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.core.Character;
import org.tendiwa.core.events.EventGetDamage;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

public class UiHealthBar extends TendiwaWidget {
private final int width;
private final Cell greenZone;
private final Cell redZone;
private final Cell blackZone;
private final int height;
private int hp;
private int maxHp;

private UiHealthBar(Observable model, Character player, ColorFillFactory colorFillFactory) {
	super();
	this.width = 200;
	this.height = 16;
	setWidth(width);
	setHeight(height);
	greenZone = add(colorFillFactory.create(Color.GREEN)).width(width / 3).height(height);
	redZone = add(colorFillFactory.create(Color.RED)).width(width / 3).height(height);
	blackZone = add(colorFillFactory.create(Color.BLACK)).width(width / 3).height(height);
	model.subscribe(new Observer<EventGetDamage>() {
		@Override
		public void update(EventGetDamage event, EventEmitter<EventGetDamage> emitter) {
			if (event.character.isPlayer()) {
				changeHp(hp - event.amount);
			}
			emitter.done(this);
		}
	}, EventGetDamage.class);
	changeHp(player.getHp());
	changeMaxHp(player.getMaxHP());
}

public void changeHp(int hp) {
	greenZone.width(getGreenZoneWidth(hp));
	invalidate();
}

private int getGreenZoneWidth(int hp) {
	return Math.max(width * hp / maxHp, 0);
}

public void changeMaxHp(int maxHp) {
	this.maxHp = maxHp;
	blackZone.width(width - getGreenZoneWidth(this.hp));
	invalidate();
}
}
