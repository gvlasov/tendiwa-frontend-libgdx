package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.google.inject.Inject;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.factories.ItemViewFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.model.QuiveredItemHolder;
import org.tendiwa.core.Item;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.observation.Finishable;
import org.tendiwa.core.observation.Observer;
import org.tendiwa.core.observation.ThreadProxy;

public class UiQuiver extends TendiwaWidget {
private final Label label;
private final ItemViewFactory itemViewFactory;
private final QuiveredItemHolder quiveredItemHolder;
private Image itemIcon;

@Inject
public UiQuiver(
	ThreadProxy model,
	ItemViewFactory itemViewFactory,
	ColorFillFactory colorFillFactory,
	FontRegistry fontRegistry,
	QuiveredItemHolder quiveredItemHolder
) {
	super();
	this.itemViewFactory = itemViewFactory;
	this.quiveredItemHolder = quiveredItemHolder;
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	label = new Label(
		Languages.getText("ui.quiver"),
		new Label.LabelStyle(fontRegistry.obtain(11, false), Color.WHITE)
	);
	itemIcon = new Image();
	add(label).pad(5);
	add(itemIcon).pad(5);
	quiveredItemHolder.addListener(new EntitySelectionListener<Item>() {
		@Override
		public void execute(Item entity) {
			update();
		}
	});
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			UiQuiver.this.update();
			emitter.done(this);
		}
	}, EventInitialTerrain.class);

}

public void update() {
	Item item = quiveredItemHolder.getItem();
	if (item == null) {
		label.setText(Languages.getText("ui.quiver_is_empty"));
		itemIcon.setDrawable(null);
	} else {
		itemIcon.setDrawable(new TextureRegionDrawable(itemViewFactory.getItemImage(item)));
		label.setText(Languages.getText("ui.quiver"));
	}
}
}
