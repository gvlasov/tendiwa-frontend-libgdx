package org.tendiwa.client.ui.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.EntitySelectionListener;
import org.tendiwa.core.Item;

import java.util.LinkedList;
import java.util.List;

@Singleton
public class QuiveredItemHolder {
private Item quiveredItem;
private List<EntitySelectionListener<Item>> listeners = new LinkedList<>();

@Inject
QuiveredItemHolder() {

}

public Item getItem() {
	return quiveredItem;
}

public void setItem(Item item) {
	quiveredItem = item;
	for (EntitySelectionListener<Item> listener : listeners) {
		listener.execute(item);
	}
}

public void addListener(EntitySelectionListener<Item> listener) {
	listeners.add(listener);
}
}
