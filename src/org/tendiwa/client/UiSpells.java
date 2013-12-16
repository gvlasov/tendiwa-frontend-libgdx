package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import org.tendiwa.events.RequestActionToCell;
import org.tendiwa.events.RequestActionWithoutTarget;
import tendiwa.core.*;

import java.lang.Character;
import java.security.MessageDigest;
import java.util.Map;

public class UiSpells extends TendiwaWidget {
private static UiSpells INSTANCE;
private static Runnable onComplete = new Runnable() {
	@Override
	public void run() {
		INSTANCE.setVisible(false);
		Gdx.input.setInputProcessor(TendiwaGame.getGameScreen().getInputProcessor());
	}
};
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<Spell> mapper = new ItemToKeyMapper<>();
private EntitySelectionListener<Spell> onActionSelected = new EntitySelectionListener<Spell>() {
	@Override
	public void execute(final Spell characterAbility) {
		final ActionTargetType action = characterAbility.getAction();
		if (action instanceof ActionToCell) {
			CellSelection.getInstance().startCellSelection(new EntitySelectionListener<EnhancedPoint>() {
				@Override
				public void execute(EnhancedPoint point) {
					Tendiwa.getServer().pushRequest(new RequestActionToCell(
						(ActionToCell) action,
						point.x,
						point.y
					));
				}
			});
		} else if (action instanceof ActionWithoutTarget) {
			Tendiwa.getServer().pushRequest(
				new RequestActionWithoutTarget(
					(ActionWithoutTarget) action
				)
			);
		}
	}
};
private Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.WHITE);

public UiSpells() {
	super();
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
}

@Override
public void update() {
	mapper.update(Tendiwa.getPlayerCharacter().getSpells());
	flowGroup.clearChildren();
	for (Map.Entry<Spell, Character> e : mapper) {
		flowGroup.addActor(createSpellWidget(e.getKey(), e.getValue()));
	}
}

private WidgetGroup createSpellWidget(Spell spell, Character character) {
	Label spellNameLabel = new Label(spell.getResourceName(), style);
	Label characterLabel = new Label(String.valueOf(character), style);
	Image image = new Image(AtlasSpells.getInstance().findRegion(spell.getResourceName()));
	Table table = new Table();
	table.add(image).padRight(10);
	table.add(characterLabel).padRight(20);
	table.add(spellNameLabel);
	table.pad(4);
	return table;
}

public static UiSpells getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new UiSpells();
	}
	return INSTANCE;
}

public InputProcessor getInputProcessor() {
	return new ItemSelectionInputProcessor<>(mapper, onActionSelected, onComplete);
}
}
