package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import org.tendiwa.client.*;
import org.tendiwa.client.ui.factories.ItemViewFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.lexeme.Language;
import org.tendiwa.core.Item;
import org.tendiwa.core.ItemPile;
import org.tendiwa.core.Items;

import java.util.Map;

public class UiItemSelectionTable extends Table {
private final ItemViewFactory itemViewFactory;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
private Label.LabelStyle labelStyle;

@Inject
public UiItemSelectionTable(FontRegistry fontRegistry, ItemViewFactory itemViewFactory) {
	this.itemViewFactory = itemViewFactory;
	add(flowGroup).expand().fill();
	setFillParent(true);
	labelStyle = new Label.LabelStyle();
	labelStyle.font = fontRegistry.obtain(14, false);
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
	Image image = itemViewFactory.createItemImage(item);
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
