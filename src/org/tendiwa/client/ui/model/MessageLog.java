package org.tendiwa.client.ui.model;

import java.util.LinkedList;

public class MessageLog {
private LinkedList<String> messages = new LinkedList<>();

public void pushMessage(String message) {
	messages.add(message);
}

public String getLastMessage() {
	return messages.getLast();
}
}
