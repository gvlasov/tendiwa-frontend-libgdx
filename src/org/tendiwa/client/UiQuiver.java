package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import tendiwa.core.Item;

public class UiQuiver extends TendiwaWidget {
private final Label label;
private Image itemIcon;

public UiQuiver() {
	super();
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	label = new Label(
		Languages.getText("ui.quiver"),
		amountStyle
	);
	itemIcon = new Image();
	add(label).pad(5);
	add(itemIcon).pad(5);
	QuiveredItemHolder.addListener(new EntitySelectionListener<Item>() {
		@Override
		public void execute(Item entity) {
			update();
		}
	});
}

@Override
public void update() {
	Item item = QuiveredItemHolder.getItem();
	if (item == null) {
		label.setText(Languages.getText("ui.quiver_is_empty"));
		itemIcon.setDrawable(null);
	} else {
		itemIcon.setDrawable(new TextureRegionDrawable(getItemImage(item)));
		label.setText(Languages.getText("ui.quiver"));
	}
}
}
