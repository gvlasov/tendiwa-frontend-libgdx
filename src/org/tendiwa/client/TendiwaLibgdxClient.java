package org.tendiwa.client;

import com.google.inject.Inject;
import org.tendiwa.core.RequestInitialTerrain;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;
import org.tendiwa.core.TendiwaClientEventManager;

public class TendiwaLibgdxClient implements TendiwaClient {
private final EventProcessor eventProcessor;
private final TendiwaClientLibgdxEventManager eventManager;

@Inject
TendiwaLibgdxClient(EventProcessor eventProcessor, TendiwaClientLibgdxEventManager eventManager) {
	this.eventProcessor = eventProcessor;
	this.eventManager = eventManager;
}

@Override
public void startup() {
	Languages.init();
	Tendiwa.getServer().pushRequest(new RequestInitialTerrain());
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
