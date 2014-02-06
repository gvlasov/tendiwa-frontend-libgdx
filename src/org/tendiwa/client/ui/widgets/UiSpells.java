package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.google.inject.Inject;
import org.tendiwa.client.*;
import org.tendiwa.client.ui.controller.SpellSelectionListener;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.factories.SpellViewFactory;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.client.ui.input.KeyboardAction;
import org.tendiwa.core.Spell;

import java.util.Collection;
import java.util.Map;

public class UiSpells extends TendiwaWidget {
private final GameScreen gameScreen;
private final SpellViewFactory spellViewFactory;
private final EntitySelectionListener<Spell> onActionSelected;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<Spell> mapper = new ItemToKeyMapper<>();
private Runnable onComplete = new Runnable() {
	@Override
	public void run() {
		setVisible(false);
		Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
	}
};

@Inject
public UiSpells(
	InputToActionMapper actionMapper,
	SpellViewFactory spellViewFactory,
	SpellSelectionListener spellSelectionListener,
	GameScreen gameScreen,
	ColorFillFactory colorFillFactory
) {
	super();
	this.spellViewFactory = spellViewFactory;
	this.onActionSelected = spellSelectionListener;
	this.gameScreen = gameScreen;
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
	actionMapper.putAction(Input.Keys.Z, new KeyboardAction("action.castMenu") {
		@Override
		public void act() {
			UiSpells.this.setVisible(true);
			Gdx.input.setInputProcessor(new EntitySelectionInputProcessor<>(mapper, onActionSelected, onComplete));
		}
	});
}

public void changeSpells(Collection<Spell> spells) {
	mapper.update(spells);
	flowGroup.clearChildren();
	for (Map.Entry<Spell, Character> e : mapper) {
		flowGroup.addActor(spellViewFactory.create(e.getKey(), e.getValue()));
	}

}

}
