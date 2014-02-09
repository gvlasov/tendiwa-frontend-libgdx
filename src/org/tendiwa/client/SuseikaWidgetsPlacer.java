package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.extensions.std.keyHints.KeyHintsWidget;
import org.tendiwa.client.extensions.std.spells.SpellsWidget;
import org.tendiwa.client.ui.widgets.*;

@Singleton
public class SuseikaWidgetsPlacer implements WidgetsPlacer {
private final UiActions actions;
private final SpellsWidget spells;
private final UiInventory inventory;
private final UiLog log;
private final UiHealthBar hpBar;
private final UiQuiver quiver;
private final KeyHintsWidget keyHintsWidget;

@Inject
SuseikaWidgetsPlacer(
	UiActions actions,
	SpellsWidget spells,
	UiInventory inventory,
	UiLog log,
	UiHealthBar hpBar,
	UiQuiver quiver,
	KeyHintsWidget keyHintsWidget

) {
	this.actions = actions;
	this.spells = spells;
	this.inventory = inventory;
	this.log = log;
	this.hpBar = hpBar;
	this.quiver = quiver;
	this.keyHintsWidget = keyHintsWidget;
}

@Override
public void placeWidgets(
	Stage stage,
	Table table
) {
	actions.setVisible(false);

	spells.setVisible(false);

//	table.add(log).width(400).height(200).expand().pad(5).left().top().colspan(2);
	table.add(hpBar).expand().right().top().pad(5).colspan(1);
	table.row();
	table.add(quiver).expand().right().bottom().pad(5).colspan(3);
	table.row();
	table.add(actions).left().bottom().pad(5).size(200, 100);
	table.add(spells).pad(5).size(200, 100);
	table.add(inventory).right().bottom().pad(5).size(200, 100);
//	inventory.update();
//	actions.update();

//	log.update();

	stage.addActor(keyHintsWidget);
//	keyHintsWidget.update();
	keyHintsWidget.setVisible(false);

}
}
