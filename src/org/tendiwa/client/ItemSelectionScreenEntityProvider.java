package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import org.tendiwa.core.Item;

class ItemSelectionScreenEntityProvider implements EntityProvider<Item> {
private final TendiwaGame game;

ItemSelectionScreenEntityProvider(TendiwaGame game) {
	this.game = game;
}

@Override
public void startSelection(ItemToKeyMapper<Item> mapper, EntityFilter<Item> filter, EntitySelectionListener<Item> onNextItemSelected) {
	inputProcessor = new ItemSelectionInputProcessor<>(mapper, onNextItemSelected, onComplete);
	table.update(mapper, filter);
	game.switchToItemSelectionScreen();
	Gdx.input.setInputProcessor(inputProcessor);
}
}
