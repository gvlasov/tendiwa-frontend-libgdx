package org.tendiwa.client;

public interface EntityProvider<T> {
public void startSelection(ItemToKeyMapper<T> mapper, EntitySelectionListener<T> onNextItemSelected);
}
