package org.tendiwa.client;

import com.badlogic.gdx.InputProcessor;

/**
 * Processes keypresses, decides which entity was chosen with a keypress and then passes that entity to code listening
 * for an entity to be chosen.
 *
 * @param <T>
 * 	Type of entity. Common examples are {@link tendiwa.core.Item}s or Spells.
 */
public class ItemSelectionInputProcessor<T> implements InputProcessor {
private ItemToKeyMapper<T> itemToKeyMapper;
private EntitySelectionListener<T> listener;
private EntitySelectionListener<T> persistentListener;

void setMapper(ItemToKeyMapper<T> itemToKeyMapper) {
	this.itemToKeyMapper = itemToKeyMapper;
}

@Override
public boolean keyDown(int keycode) {
	return false;
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	T itemForCharacter = itemToKeyMapper.getItemForCharacter(character);
	if (itemForCharacter != null) {
		persistentListener.execute(itemForCharacter);
		listener.execute(itemForCharacter);
	}
	return true;
}

/**
 * Sets a listener that is executed once after an entity was chosen, and then is removed from this
 * ItemSelectionInputProcessor.
 */
public void setListener(EntitySelectionListener<T> listener) {
	this.listener = listener;
}
void setPersistentListener(EntitySelectionListener<T> listener) {
	this.persistentListener = listener;
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
