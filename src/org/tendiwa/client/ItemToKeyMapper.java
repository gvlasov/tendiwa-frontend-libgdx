package org.tendiwa.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.*;

/**
 * Maps items (e.g. in player's inventory/equipment, or lying on the same cell on the floor, or in the same container
 * etc.) to keys. To use this class, give its {@link ItemToKeyMapper#update(Iterable)} method the collection of objects
 * you want to observe. After {@link ItemToKeyMapper#update(Iterable)} call, ItemToKeyMapper will create new mappings
 * for items added to collection, and will remove mappings to items removed from collection.
 */
public class ItemToKeyMapper<T> implements Iterable<Map.Entry<T, Character>> {
private static final char[] charsMappingOrder = new char[]{
	'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
};
BiMap<T, Character> itemsToChars = HashBiMap.create();
List<Character> freeCharacters = new ArrayList<>();
private Comparator<Character> comparator = new Comparator<Character>() {

	@Override
	public int compare(Character o1, Character o2) {
		if (o1.charValue() == o2.charValue()) {
			return 0;
		}
		boolean upperCase1 = Character.isUpperCase(o1);
		boolean upperCase2 = Character.isUpperCase(o2);
		if (upperCase1 && !upperCase2) {
			return 1;
		}
		if (upperCase2 && !upperCase1) {
			return -1;
		}
		if (o1 > o2) {
			return 1;
		} else {
			return -1;
		}
	}
};

public ItemToKeyMapper() {
	for (char ch : charsMappingOrder) {
		freeCharacters.add(ch);
	}
}

/**
 * For each item in {@code items}, creates a mapping to key if there is no mapping from that item. Then, for each
 * existing mapping, removes that mapping if the item of that mapping is not in the collection any more.
 *
 * @param items
 * 	Any collection.
 */
public void update(Iterable<T> items) {
	Collection<T> copyToCheckIfContains = new HashSet<>();
	for (T item : items) {
		if (!itemsToChars.containsKey(item)) {
			// Add new objects to map
			itemsToChars.put(item, obtainNextFreeChar());
		}
		copyToCheckIfContains.add(item);
	}
	for (Iterator<T> iterator = itemsToChars.keySet().iterator(); iterator.hasNext(); ) {
		T item = iterator.next();
		if (!copyToCheckIfContains.contains(item)) {
			freeChar(itemsToChars.get(item));
			iterator.remove();
		}
	}

}

/**
 * Frees a char mapped to an object.
 *
 * @param ch
 * 	A char mapped to some object
 */
private void freeChar(char ch) {
	assert itemsToChars.containsValue(ch);
	freeCharacters.add(ch);
	Collections.sort(freeCharacters, comparator);
}

/**
 * Returns a char that is not mapped to any object. Chars are obtained in the alphabetical order, first all lower-case,
 * then all upper-case. Returned char automatically goes out from the set of free chars.
 *
 * @return A character that is not mapped to any object yet.
 */
private char obtainNextFreeChar() {
	if (freeCharacters.isEmpty()) {
		throw new RuntimeException("No free characters left in a mapper");
	}
	Character next = freeCharacters.iterator().next();
	boolean removed = freeCharacters.remove(next);
	assert removed;
	return next;
}

/**
 * Returns a object mapped to that char.
 *
 * @param ch
 * 	A case-sensitive char pressed on keyboard.
 * @return An object mapped to that char, or null if there's no object mapped to that character.
 */
public T getItemForCharacter(char ch) {
	return itemsToChars.inverse().get(ch);
}

@Override
public Iterator<Map.Entry<T, Character>> iterator() {
	return itemsToChars.entrySet().iterator();
}
}
