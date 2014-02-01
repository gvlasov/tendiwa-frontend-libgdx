package org.tendiwa.client;

import com.google.inject.Inject;
import org.tendiwa.core.Server;
import org.tendiwa.core.Tendiwa;

public class EventProcessor {
private final TendiwaClientLibgdxEventManager eventManager;
private final TaskManager taskManager;
private boolean eventResultProcessingIsGoing = false;
private boolean lastEventEndsFrame;
private int eventsProcessed;

@Inject
EventProcessor(TendiwaClientLibgdxEventManager eventManager, TaskManager taskManager) {

	this.eventManager = eventManager;
	this.taskManager = taskManager;
}

void processEvents() {
	if (!eventResultProcessingIsGoing
		&& !Server.hasRequestToProcess()
		&& !eventManager.hasResultPending()
		&& taskManager.hasCurrentTask()
		) {
		taskManager.executeCurrentTask();
	}
	// Loop variable will remain true if it is not set to true inside .process().
	while (!eventResultProcessingIsGoing && eventManager.hasResultPending()) {
		EventResult result = eventManager.provideEventResult();
		eventResultProcessingIsGoing = true;
		// lastEventEndsFrame will remain true under the same condition
		lastEventEndsFrame = true;
		result.process();
		eventsProcessed++;
	}
}

void processEventsUntilRenderNecessity() {
	eventsProcessed = 0;
	do {
		synchronized (Tendiwa.getLock()) {
			processEvents();
			// If at least one event was processed during this frame,
			// then be ready to process more events that come from the last request
			if (eventsProcessed > 0) {
				// If the next event after the last event is supposed to be rendered in current frame,
				// then wait until a pending operation comes to client.
				while (!eventManager.hasResultPending() && !lastEventEndsFrame) {
					try {
						// While this thread waits, a new pending operation may be created.
						Tendiwa.getLock().wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
	while (!lastEventEndsFrame && eventManager.hasResultPending());
}

void processOneMoreEventInCurrentFrame() {
	lastEventEndsFrame = false;
}

public void signalEventProcessingDone() {
	eventResultProcessingIsGoing = false;
	Tendiwa.signalAnimationCompleted();
}

boolean isEventProcessingGoing() {
	return eventResultProcessingIsGoing;
}
}