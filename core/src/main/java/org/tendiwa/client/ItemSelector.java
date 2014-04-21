package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.google.inject.Inject;
import org.tendiwa.client.extensions.std.itemSelection.ItemSelectionScreen;
import org.tendiwa.client.extensions.std.itemSelection.ItemSelectionScreenOnComplete;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.Item;

public class ItemSelector {
private final ItemSelectionScreen itemSelectionScreen;
private final UiModeManager uiModeManager;
private final Game game;
private final Runnable onComplete;

@Inject
ItemSelector(
	Game game,
	ItemSelectionScreenOnComplete onComplete,
	ItemSelectionScreen itemSelectionScreen,
	UiModeManager uiModeManager
) {
	this.game = game;
	this.onComplete = onComplete;
	this.itemSelectionScreen = itemSelectionScreen;
	this.uiModeManager = uiModeManager;
}

public void startSelection(
	ItemToKeyMapper<Item> mapper,
	EntityFilter<Item> filter,
	EntitySelectionListener<Item> onNextItemSelected
) {
	UiMode inputProcessor = new EntitySelectionInputProcessor<>(
		mapper,
		onNextItemSelected,
		onComplete
	);
	itemSelectionScreen.updateTable(mapper, filter);
	game.setScreen(itemSelectionScreen);
	uiModeManager.pushMode(inputProcessor);

}
}
