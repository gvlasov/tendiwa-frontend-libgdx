package org.tendiwa.client.ui.factories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.TransitionsToFloor;
import org.tendiwa.core.FloorType;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class FloorTransitionsProvidersRegistry {
private final TransitionsToFloorFactory factory;
private Map<FloorType, TransitionsToFloor> floorTransitionsProviders = new HashMap<>();

@Inject
FloorTransitionsProvidersRegistry(TransitionsToFloorFactory factory) {
	this.factory = factory;
}

public TransitionsToFloor obtain(FloorType floorType) {
	TransitionsToFloor transitionsToFloor = floorTransitionsProviders.get(floorType);
	if (transitionsToFloor == null) {
		transitionsToFloor = factory.create(floorType);
		floorTransitionsProviders.put(floorType, transitionsToFloor);
	}
	return transitionsToFloor;
}
}
