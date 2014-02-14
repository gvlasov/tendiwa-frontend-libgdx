package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.google.inject.Inject;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.VerticalFlowGroup;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.factories.ItemViewFactory;
import org.tendiwa.core.*;
import org.tendiwa.core.events.*;
import org.tendiwa.core.observation.Finishable;
import org.tendiwa.core.observation.Observer;
import org.tendiwa.core.observation.ThreadProxy;
import org.tendiwa.core.volition.Volition;

public class UiInventory extends TendiwaWidget {
private final Volition volition;
private final ItemViewFactory itemViewFactory;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
private ItemCollection inventory;
private Equipment equipment;

@Inject
public UiInventory(
	ThreadProxy model,
	Volition volition,
	ColorFillFactory colorFillFactory,
	ItemViewFactory itemViewFactory
) {
	super();
	this.volition = volition;
	this.itemViewFactory = itemViewFactory;
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	setSize(400, 300);
	add(flowGroup).expand().fill();
	model.subscribe(new Observer<EventGetItem>() {
		@Override
		public void update(EventGetItem event, Finishable<EventGetItem> emitter) {
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventGetItem.class);
	model.subscribe(new Observer<EventLoseItem>() {
		@Override
		public void update(EventLoseItem event, Finishable<EventLoseItem> emitter) {
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventLoseItem.class);
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			UiInventory.this.equipment = event.player.getEquipment();
			UiInventory.this.inventory = event.player.getInventory();
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventInitialTerrain.class);
	model.subscribe(new Observer<EventWield>() {
		@Override
		public void update(EventWield event, Finishable<EventWield> emitter) {
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventWield.class);
	model.subscribe(new Observer<EventPutOn>() {
		@Override
		public void update(EventPutOn event, Finishable<EventPutOn> emitter) {
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventPutOn.class);
	model.subscribe(new Observer<EventTakeOff>() {
		@Override
		public void update(EventTakeOff event, Finishable<EventTakeOff> emitter) {
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventTakeOff.class);
	model.subscribe(new Observer<EventUnwield>() {
		@Override
		public void update(EventUnwield event, Finishable<EventUnwield> emitter) {
			UiInventory.this.update();
			emitter.done(this);
		}
	}, EventUnwield.class);
}

public void update() {
	flowGroup.clearChildren();
	assert equipment != null;
	assert inventory != null;
	for (final Item item : equipment) {
		Widget itemIcon = itemViewFactory.createItemImage(item);
		itemIcon.setColor(Color.RED);
		itemIcon.addListener(new EquippedClickListener(item));
		flowGroup.addActor(itemIcon);
	}
	for (final Item item : inventory) {
		Actor itemIcon;
		if (item.getType().isStackable()) {
			itemIcon = itemViewFactory.createItemPileIcon((ItemPile) item);
		} else {
			itemIcon = itemViewFactory.createItemImage(item);
		}
		itemIcon.addListener(new UnequippedClickListener(item));
		flowGroup.addActor(itemIcon);
	}
}

private class EquippedClickListener extends InputListener {
	private final Item item;

	public EquippedClickListener(Item item) {
		this.item = item;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		if (Items.isWearable(item.getType())) {
			volition.takeOff((UniqueItem) item);
		} else {
			volition.unwield(item);
		}
		return true;
	}
}

private class UnequippedClickListener extends InputListener {
	private final Item item;

	public UnequippedClickListener(Item item) {
		this.item = item;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			volition.drop(item);
		} else {
			if (Items.isWearable(item.getType())) {
				if (equipment.canPutOn((UniqueItem) item)) {
					volition.putOn((UniqueItem) item);
				}
			} else {
				if (equipment.canWield(item)) {
					volition.wield(item);
				}
			}
		}
		return true;
	}
}
}
