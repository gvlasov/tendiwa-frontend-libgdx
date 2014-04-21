package org.tendiwa.client;

public interface EntityProvider<T> {
public void startSelection(ItemToKeyMapper<T> mapper, EntityFilter<T> filter, EntitySelectionListener<T> onNextItemSelected);
}
