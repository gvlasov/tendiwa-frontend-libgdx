package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import tendiwa.core.Spell;
import tendiwa.core.Tendiwa;

import java.util.Map;

public class UiSpells extends TendiwaWidget {
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<Spell> mapper = new ItemToKeyMapper<>();
private Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.WHITE);

public UiSpells() {
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
}
