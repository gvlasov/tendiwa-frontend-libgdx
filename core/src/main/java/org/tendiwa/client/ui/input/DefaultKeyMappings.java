package org.tendiwa.client.ui.input;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class DefaultKeyMappings {

private final InputToActionMapper mapper;

@Inject
DefaultKeyMappings(
	@Named("default") InputToActionMapper mapper
) {
	this.mapper = mapper;
}

public void addMapping(InputMapping mapping, NonPointerAction action) {
	mapper.addMapping(mapping, action);
}
}
