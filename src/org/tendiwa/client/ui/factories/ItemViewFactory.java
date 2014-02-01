package org.tendiwa.client.ui.factories;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import org.tendiwa.client.AtlasItems;
import org.tendiwa.client.TextureRegionFlipper;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.core.Equipment;
import org.tendiwa.core.Item;
import org.tendiwa.core.ItemPile;

public class ItemViewFactory {
private final Label.LabelStyle amountStyle;

ItemViewFactory(FontRegistry fontRegistry) {
	amountStyle = new Label.LabelStyle(fontRegistry.obtain(18, false), Color.WHITE);
}

public TextureRegion getItemImage(Item item) {
	assert item != null;
	return TextureRegionFlipper.flip(AtlasItems.getInstance().findRegion(item.getType().getResourceName()));
}

/**
 * Creates an item icon with amount number.
 *
 * @param item
 * 	An item pile
 * @return Icon with amount drawn on it.
 */
public WidgetGroup createItemPileIcon(ItemPile item) {
	Table table = new Table();
	table.setBackground(createItemImage(item).getDrawable());
	table.add(
		new Label(Integer.toString(item.getAmount()), amountStyle)
	).top().left().expand();
	return table;
}

public Image createItemImage(final Item item) {
	assert item != Equipment.nullItem;
	return new Image(getItemImage(item));
}
}
