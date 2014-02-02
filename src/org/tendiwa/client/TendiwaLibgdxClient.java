package org.tendiwa.client;

import com.google.inject.Inject;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.TendiwaClientEventManager;
import org.tendiwa.core.Volition;

public class TendiwaLibgdxClient implements TendiwaClient {
private final EventProcessor eventProcessor;
private final TendiwaClientLibgdxEventManager eventManager;
private final Volition volition;

@Inject
TendiwaLibgdxClient(EventProcessor eventProcessor, TendiwaClientLibgdxEventManager eventManager, Volition volition) {
	this.eventProcessor = eventProcessor;
	this.eventManager = eventManager;
	this.volition = volition;
}

@Override
public void startup() {
	Languages.init();
	volition.requestSurroundings();
	eventProcessor.processEvents();
}

@Override
public TendiwaClientEventManager getEventManager() {
	return eventManager;
}

@Override
public boolean isAnimationCompleted() {
	return !eventManager.hasResultPending() && !eventProcessor.isEventProcessingGoing();
}
}
