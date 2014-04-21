package org.tendiwa.client.extensions.std;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.extensions.std.quiver.UiQuiver;
import org.tendiwa.client.extensions.std.actions.Actions;
import org.tendiwa.client.extensions.std.keyHints.KeyHints;
import org.tendiwa.client.extensions.std.spells.Spells;
import org.tendiwa.client.ui.WidgetsPlacer;
import org.tendiwa.client.extensions.std.hpBar.UiHealthBar;
import org.tendiwa.client.ui.widgets.UiInventory;
import org.tendiwa.client.extensions.std.log.UiLog;

@Singleton
public class SuseikaWidgetsPlacer implements WidgetsPlacer {
private final Actions actions;
private final Spells spells;
private final UiInventory inventory;
private final UiLog log;
private final UiHealthBar hpBar;
private final UiQuiver quiver;
private final KeyHints keyHints;

@Inject
SuseikaWidgetsPlacer(
	Actions actions,
	Spells spells,
	UiInventory inventory,
	UiLog log,
	UiHealthBar hpBar,
	UiQuiver quiver,
	KeyHints keyHints
) {
	this.actions = actions;
	this.spells = spells;
	this.inventory = inventory;
	this.log = log;
	this.hpBar = hpBar;
	this.quiver = quiver;
	this.keyHints = keyHints;
}

@Override
public void placeWidgets(
	Stage stage,
	Table table
) {
	actions.configure();
	spells.configure();
	keyHints.configure();
	actions.getWidget().setVisible(false);
	spells.getWidget().setVisible(false);

	table.add(log).width(400).height(200).expand().pad(5).left().top().colspan(2);
	table.add(hpBar).expand().right().top().pad(5).colspan(1);
	table.row();
	table.add(quiver).expand().right().bottom().pad(5).colspan(3);
	table.row();
	table.add(actions.getWidget()).left().bottom().pad(5).size(200, 100);
	table.add(spells.getWidget()).pad(5).size(200, 100);
	table.add(inventory).right().bottom().pad(5).size(200, 100);
//	inventory.update();
//	actions.update();

//	log.update();

	stage.addActor(keyHints.getWidget());
//	keyHintsWidget.update();
	keyHints.getWidget().setVisible(false);

}
}
