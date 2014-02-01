package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.google.inject.Inject;
import org.tendiwa.core.Item;

public class ItemSelector {
private final ItemSelectionScreen itemSelectionScreen;
private final Game game;
private final ItemSelectionScreenOnComplete onComplete;

@Inject
ItemSelector(Game game, ItemSelectionScreenOnComplete onComplete, ItemSelectionScreen itemSelectionScreen) {
	this.game = game;
	this.onComplete = onComplete;
	this.itemSelectionScreen = itemSelectionScreen;
}

public void startSelection(ItemToKeyMapper<Item> mapper, EntityFilter<Item> filter, EntitySelectionListener<Item> onNextItemSelected) {
	EntitySelectionInputProcessor<Item> inputProcessor = new EntitySelectionInputProcessor<>(mapper, onNextItemSelected, onComplete);
	itemSelectionScreen.updateTable(mapper, filter);
	game.setScreen(itemSelectionScreen);
	Gdx.input.setInputProcessor(inputProcessor);
}
}
