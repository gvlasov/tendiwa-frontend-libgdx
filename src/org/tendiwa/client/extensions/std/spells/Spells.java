package org.tendiwa.client.extensions.std.spells;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.EntitySelectionInputProcessor;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.ui.UiPlugin;
import org.tendiwa.client.ui.cellSelection.CellSelectionActor;
import org.tendiwa.client.ui.controller.SpellSelectionListener;
import org.tendiwa.client.ui.input.DefaultKeyMappings;
import org.tendiwa.client.ui.input.KeyboardAction;
import org.tendiwa.client.ui.input.KeyboardInputMapping;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.Spell;

public class Spells implements UiPlugin {
private final DefaultKeyMappings defaultKeyMappings;
private final SpellsWidget widget;
private final UiModeManager uiModeManager;
private final ItemToKeyMapper<Spell> mapper;
private final SpellSelectionListener spellSelectionListener;
private final CellSelectionActor cellSelectionActor;

@Inject
public Spells(
	DefaultKeyMappings defaultKeyMappings,
	SpellsWidget widget,
	UiModeManager uiModeManager,
	@Named("spells_widget") ItemToKeyMapper<Spell> mapper,
	SpellSelectionListener spellSelectionListener,
	CellSelectionActor cellSelectionActor
) {
	this.defaultKeyMappings = defaultKeyMappings;
	this.widget = widget;
	this.uiModeManager = uiModeManager;
	this.mapper = mapper;
	this.spellSelectionListener = spellSelectionListener;
	this.cellSelectionActor = cellSelectionActor;
}

@Override
public void configure() {
	defaultKeyMappings.addMapping(
		new KeyboardInputMapping(Input.Keys.Z),
		new KeyboardAction("ui.actions.spells.show_spell_list") {
			@Override
			public void act() {
				widget.setVisible(true);
				uiModeManager.pushMode(new EntitySelectionInputProcessor<>(
					mapper,
					spellSelectionListener,
					new Runnable() {
						@Override
						public void run() {
							widget.setVisible(false);
							uiModeManager.popMode();
						}
					}
				));
			}
		}
	);
}

@Override
public Actor getWidget() {
	return widget;
}
}
