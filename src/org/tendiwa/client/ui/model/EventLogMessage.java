package org.tendiwa.client.ui.model;

import org.tendiwa.core.observation.Event;

public class EventLogMessage implements Event {
public final String message;

public EventLogMessage(String message) {
	this.message = message;
}
}
