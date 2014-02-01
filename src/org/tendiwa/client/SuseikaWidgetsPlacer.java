package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.widgets.*;

public class SuseikaWidgetsPlacer implements WidgetsPlacer {
private final Injector injector;
private FontRegistry fontRegistry;

@Inject
SuseikaWidgetsPlacer(Injector injector) {

	this.injector = injector;
}

@Override
public void placeWidgets(Stage stage, Table table) {
	UiActions actions = injector.getInstance(UiActions.class);
	actions.setVisible(false);

	UiSpells spells = injector.getInstance(UiSpells.class);
	spells.setVisible(false);

	UiInventory inventory = injector.getInstance(UiInventory.class);
	UiLog log = injector.getInstance(UiLog.class);
	UiHealthBar hpBar = injector.getInstance(UiHealthBar.class);
//	table.add(log).width(400).height(200).expand().pad(5).left().top().colspan(2);
	table.add(hpBar).expand().right().top().pad(5).colspan(1);
	table.row();
	UiQuiver uiQuiver = injector.getInstance(UiQuiver.class);
	table.add(uiQuiver).expand().right().bottom().pad(5).colspan(3);
	table.row();
	table.add(actions).left().bottom().pad(5).size(200, 100);
	table.add(spells).pad(5).size(200, 100);
	table.add(inventory).right().bottom().pad(5).size(200, 100);
	inventory.update();
	actions.update();
//	log.update();

	UiKeyHints uiKeyHints = injector.getInstance(UiKeyHints.class);
	stage.addActor(uiKeyHints);
	uiKeyHints.update();
	uiKeyHints.setVisible(false);

}
}
