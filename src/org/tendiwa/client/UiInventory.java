package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import org.tendiwa.events.*;
import tendiwa.core.*;

public class UiInventory extends Table {
private static final BitmapFont font = new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf")).generateFont(8, "1234567890", false);
private static final Label.LabelStyle amountStyle = new Label.LabelStyle(font, Color.WHITE);
VerticalFlowGroup flowGroup = new VerticalFlowGroup();

public UiInventory() {
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	setSize(400, 300);
	add(flowGroup).expand().fill();
}

public static Image createItemIcon(final Item item) {
	assert item != Equipment.nullItem;
	return new Image(TextureRegionFlipper.flip(AtlasItems.getInstance().findRegion(item.getType().getResourceName())));
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

public void update() {
	flowGroup.clearChildren();
	for (final Item item : Tendiwa.getPlayerCharacter().getEquipment()) {
		Widget itemIcon = createItemIcon(item);
		itemIcon.setColor(Color.RED);
		itemIcon.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (item.getType().isWearable()) {
					Tendiwa.getServer().pushRequest(new RequestTakeOff((UniqueItem) item));
				} else {
					Tendiwa.getServer().pushRequest(new RequestUnwield(item));
				}
				return true;
			}
		});
		flowGroup.addActor(itemIcon);
	}
	for (final Item item : Tendiwa.getPlayerCharacter().getInventory()) {
		Actor itemIcon;
		if (item.getType().isStackable()) {
			itemIcon = createItemPileIcon((ItemPile) item);
		} else {
			itemIcon = createItemIcon(item);
		}
		itemIcon.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					Tendiwa.getServer().pushRequest(new RequestDrop(item));
				} else {
					if (item.getType().isWearable()) {
						if (Tendiwa.getPlayerCharacter().getEquipment().canPutOn((UniqueItem) item)) {
							Tendiwa.getServer().pushRequest(new RequestPutOn((UniqueItem) item));
						}
					} else {
						if (Tendiwa.getPlayerCharacter().getEquipment().canWield(item)) {
							Tendiwa.getServer().pushRequest(new RequestWield(item));
						}
					}
				}
				return true;
			}
		});
		flowGroup.addActor(itemIcon);
	}
}

}
