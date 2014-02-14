package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.tablelayout.Cell;
import com.google.inject.Inject;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.core.events.EventGetDamage;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.observation.Finishable;
import org.tendiwa.core.observation.Observer;
import org.tendiwa.core.observation.ThreadProxy;
import org.tendiwa.core.player.SinglePlayerMode;

public class UiHealthBar extends TendiwaWidget {
private final int width;
private final Cell greenZone;
private final Cell redZone;
private final Cell blackZone;
private final int height;
private int hp;
private int maxHp;

@Inject
private UiHealthBar(
	ThreadProxy model,
	ColorFillFactory colorFillFactory,
	final SinglePlayerMode singlePlayerMode
) {
	super();
	this.width = 200;
	this.height = 16;
	setWidth(width);
	setHeight(height);
	greenZone = add(colorFillFactory.create(Color.GREEN)).width(width / 3).height(height);
	redZone = add(colorFillFactory.create(Color.RED)).width(0).height(height);
	blackZone = add(colorFillFactory.create(Color.BLACK)).width(width / 3).height(height);
	model.subscribe(new Observer<EventGetDamage>() {
		@Override
		public void update(EventGetDamage event, Finishable<EventGetDamage> emitter) {
			if (singlePlayerMode.isPlayer(event.character)) {
				changeHp(hp - event.amount);
			}
			emitter.done(this);
		}
	}, EventGetDamage.class);
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			changeMaxHp(event.player.getMaxHP());
			changeHp(event.player.getHp());
			emitter.done(this);
		}
	}, EventInitialTerrain.class);

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
