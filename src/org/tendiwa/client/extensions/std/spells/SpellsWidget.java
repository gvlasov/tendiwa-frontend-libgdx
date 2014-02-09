package org.tendiwa.client.extensions.std.spells;

import com.badlogic.gdx.graphics.Color;
import com.google.inject.Inject;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.VerticalFlowGroup;
import org.tendiwa.client.ui.controller.SpellSelectionListener;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.factories.SpellViewFactory;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.core.Spell;

import java.util.Collection;
import java.util.Map;

public class SpellsWidget extends TendiwaWidget {
private final InputToActionMapper actionMapper;
private final SpellViewFactory spellViewFactory;
private final EntitySelectionListener<Spell> onActionSelected;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<Spell> mapper = new ItemToKeyMapper<>();

@Inject
public SpellsWidget(
	InputToActionMapper actionMapper,
	SpellViewFactory spellViewFactory,
	SpellSelectionListener spellSelectionListener,
	ColorFillFactory colorFillFactory
) {
	super();
	this.actionMapper = actionMapper;
	this.spellViewFactory = spellViewFactory;
	this.onActionSelected = spellSelectionListener;
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
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
