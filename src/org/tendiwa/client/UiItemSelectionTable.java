package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import tendiwa.core.Item;

import java.util.Map;

public class UiItemSelectionTable extends Table {
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
private Label.LabelStyle labelStyle;

public UiItemSelectionTable() {
	add(flowGroup).expand().fill();
	setFillParent(true);
	labelStyle = new Label.LabelStyle();
	labelStyle.font = TendiwaFonts.default14NonFlipped;
}

public void update(ItemToKeyMapper<Item> mappings) {
	assert mappings != null;
	flowGroup.clearChildren();
	for (Map.Entry<Item, Character> e : mappings) {
		flowGroup.addActor(createItemLetterMappingView(e.getKey(), e.getValue()));
	}
}

protected Table createItemLetterMappingView(Item item, char ch) {
	Table table = new Table();
	Image image = UiInventory.createItemIcon(item);
	Label character = new Label(String.valueOf(ch), labelStyle);
	Label itemName = new Label(item.getType().getResourceName(), labelStyle);
	table.add(image);
	table.add(character).padRight(16).padLeft(10);
	table.add(itemName);
	table.layout();
	return table;
}
}
