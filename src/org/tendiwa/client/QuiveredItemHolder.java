package org.tendiwa.client;

import tendiwa.core.Item;

import java.util.LinkedList;
import java.util.List;

public class QuiveredItemHolder {
private static Item quiveredItem;
private static List<EntitySelectionListener<Item>> listeners = new LinkedList<>();

public static Item getItem() {
	return quiveredItem;
}

public static void setItem(Item item) {
	quiveredItem = item;
	for (EntitySelectionListener<Item> listener : listeners) {
		listener.execute(item);
	}
}

public static void addListener(EntitySelectionListener<Item> listener) {
	listeners.add(listener);
}
}
