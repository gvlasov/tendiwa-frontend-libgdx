package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.google.inject.Inject;
import org.tendiwa.client.*;
import org.tendiwa.client.ui.controller.SpellSelectionListener;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.factories.SpellViewFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.core.Spell;

import java.util.Collection;
import java.util.Map;

public class UiSpells extends TendiwaWidget {
private final CursorPosition cellSelection;
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
public UiSpells(TendiwaInputProcessor tendiwaInputProcessor, SpellViewFactory spellViewFactory, SpellSelectionListener spellSelectionListener, CursorPosition cellSelection, GameScreen gameScreen, FontRegistry fontRegistry, ColorFillFactory colorFillFactory) {
	super(UiPortion.SPELLS);
	this.spellViewFactory = spellViewFactory;
	this.onActionSelected = spellSelectionListener;
	this.cellSelection = cellSelection;
	this.gameScreen = gameScreen;
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
	tendiwaInputProcessor.putAction(Input.Keys.Z, new UiAction("action.castMenu") {
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

@Override
public void onShow() {
}

}
