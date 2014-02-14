package org.tendiwa.client.extensions.std.spells;

import com.badlogic.gdx.graphics.Color;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.VerticalFlowGroup;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.factories.SpellViewFactory;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.core.Spell;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.observation.Finishable;
import org.tendiwa.core.observation.Observer;
import org.tendiwa.core.observation.ThreadProxy;

import java.util.Collection;
import java.util.Map;

@Singleton
public class SpellsWidget extends TendiwaWidget {
private final InputToActionMapper actionMapper;
private final SpellViewFactory spellViewFactory;
private final VerticalFlowGroup flowGroup = new VerticalFlowGroup();
private final ItemToKeyMapper<Spell> mapper;

@Inject
public SpellsWidget(
	InputToActionMapper actionMapper,
	SpellViewFactory spellViewFactory,
	ColorFillFactory colorFillFactory,
	@Named("spells_widget") ItemToKeyMapper<Spell> mapper,
    ThreadProxy model
) {
	super();
	this.actionMapper = actionMapper;
	this.spellViewFactory = spellViewFactory;
	this.mapper = mapper;
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			changeSpells(event.player.getSpells());
			emitter.done(this);
		}
	}, EventInitialTerrain.class);

}

public void changeSpells(Collection<Spell> spells) {
	mapper.update(spells);
	flowGroup.clearChildren();
	for (Map.Entry<Spell, Character> e : mapper) {
		flowGroup.addActor(spellViewFactory.create(e.getKey(), e.getValue()));
	}
}

InputToActionMapper getMapper() {
	return actionMapper;
}

}
