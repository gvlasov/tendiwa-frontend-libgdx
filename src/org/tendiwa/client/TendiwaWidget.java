package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import tendiwa.core.Equipment;
import tendiwa.core.Item;
import tendiwa.core.ItemPile;

public abstract class TendiwaWidget extends Table {
protected TendiwaWidget() {
	debug();
}

protected static final Label.LabelStyle amountStyle = new Label.LabelStyle(TendiwaFonts.default8NonFlipped, Color.WHITE);

public static Image createItemIcon(final Item item) {
	assert item != Equipment.nullItem;
	return new Image(getItemImage(item));
}

public static TextureRegion getItemImage(Item item) {
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
public static Table createItemPileIcon(ItemPile item) {
	Table table = new Table();
	table.setBackground(createItemIcon(item).getDrawable());
	table.add(
		new Label(Integer.toString(item.getAmount()), amountStyle)
	).top().left().expand();
	return table;
}

public abstract void update();
}
