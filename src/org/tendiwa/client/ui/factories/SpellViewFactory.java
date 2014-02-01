package org.tendiwa.client.ui.factories;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.AtlasSpells;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.core.Spell;

@Singleton
public class SpellViewFactory {
private final Label.LabelStyle style;

@Inject
SpellViewFactory(FontRegistry fontRegistry) {
	this.style = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
}

public WidgetGroup create(Spell spell, Character character) {
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
