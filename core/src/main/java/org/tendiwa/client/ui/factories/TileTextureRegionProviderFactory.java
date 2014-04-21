package org.tendiwa.client.ui.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.client.TileTextureRegionProvider;

public interface TileTextureRegionProviderFactory {
public TileTextureRegionProvider create(
	@Assisted("numberOfRegions") int numberOfRegions,
	@Assisted("regionWidth") int regionWidth,
	@Assisted("regionHeight") int regionHeight
);
}
