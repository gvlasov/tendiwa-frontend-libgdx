package org.tendiwa.client;

import tendiwa.core.Item;

public class QuiveredItemHolder {
private static Item qItem;

public static void setItem(Item item) {
	qItem = item;
}
public static Item getItem() {
	return qItem;
}
}
