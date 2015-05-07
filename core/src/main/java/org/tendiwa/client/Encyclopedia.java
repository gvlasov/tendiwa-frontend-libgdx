package org.tendiwa.client;

import org.tendiwa.core.FloorType;
import org.tendiwa.core.WallType;

import java.util.Set;

public interface Encyclopedia {
	public Set<FloorType> floors();

	public WallType wallByName(String name);
}
