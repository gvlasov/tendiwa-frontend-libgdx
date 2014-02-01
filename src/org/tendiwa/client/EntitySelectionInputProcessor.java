package org.tendiwa.client;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Processes keypresses, decides which entity was chosen with a keypress and then passes that entity to code listening
 * for an entity to be chosen.
 *
 * @param <T>
 * 	Type of entity. Common examples are {@link org.tendiwa.core.Item}s or Spells.
 */
public class EntitySelectionInputProcessor<T> implements InputProcessor {
private final ItemToKeyMapper<T> itemToKeyMapper;
private final EntitySelectionListener<T> onNextItemSelected;
private final Runnable onComplete;

/**
 * Processes keypresses when user selects an item from a list of items each of which can be selected by pressing a
 * [a-zA-Z] key.
 *
 * @param itemToKeyMapper
 * 	A mapping from items to keys. If you need to update it, you should do that manually from outside this class.
 * @param onNextItemSelected
 * 	A listener that is executed once after an item was selected.
 * @param onComplete
 * 	A listener that is executed when selection was cancelled or done. If selection was successfully done (i.e. not
 * 	cancelled), then it is executed <b>before</b> {@code onNextItemSelected}.
 */
public EntitySelectionInputProcessor(ItemToKeyMapper<T> itemToKeyMapper, EntitySelectionListener<T> onNextItemSelected, Runnable onComplete) {
	this.itemToKeyMapper = itemToKeyMapper;
	this.onNextItemSelected = onNextItemSelected;
	this.onComplete = onComplete;
}

@Override
public boolean keyDown(int keycode) {
	if (keycode == Input.Keys.ESCAPE) {
		onComplete.run();
	}
	return true;
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	if (character == '-') {
		onNextItemSelected.execute(null);
	}
	T itemForCharacter = itemToKeyMapper.getItemForCharacter(character);
	if (itemForCharacter != null) {
		onComplete.run();
		onNextItemSelected.execute(itemForCharacter);
	}
	return true;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	return false;
}

@Override
public boolean scrolled(int amount) {
	return false;
}
}
