package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.tendiwa.lexeme.Language;
import tendiwa.core.Item;
import tendiwa.core.ItemPile;
import tendiwa.core.Items;

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

public void update(ItemToKeyMapper<Item> mappings, EntityFilter<Item> filter) {
	assert mappings != null;
	flowGroup.clearChildren();
	for (Map.Entry<Item, Character> e : mappings) {
		if (filter.check(e.getKey())) {
			flowGroup.addActor(createItemLetterMappingView(e.getKey(), e.getValue()));
		}
	}
}

protected Table createItemLetterMappingView(Item item, char ch) {
	Table table = new Table();
	Image image = UiInventory.createItemIcon(item);
	Label character = new Label(String.valueOf(ch), labelStyle);
	String labelText;
	if (Items.isStackable(item)) {
		ItemPile itemPile = Items.asStackable(item);
		labelText = Languages.getText(
			"inventory.item_pile_name",
			Language.number(itemPile.getAmount()),
			item
		);
	} else {
		labelText = Languages.getWord(item, true);
	}
	Label itemName = new Label(labelText, labelStyle);
	table.add(image);
	table.add(character).padRight(16).padLeft(10);
	table.add(itemName);
	table.layout();
	return table;
}
}
