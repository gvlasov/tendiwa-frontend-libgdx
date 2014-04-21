package org.tendiwa.client.ui.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.core.observation.Observable;

import java.util.LinkedList;

@Singleton
public class MessageLog extends Observable {
private LinkedList<String> messages = new LinkedList<>();

@Inject
MessageLog() {
	createEventEmitter(EventLogMessage.class);
}

public void pushMessage(String message) {
	messages.add(message);
	emitEvent(new EventLogMessage(message));
}

public String getLastMessage() {
	return messages.getLast();
}
}
