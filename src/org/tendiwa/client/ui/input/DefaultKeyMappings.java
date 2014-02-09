package org.tendiwa.client.ui.input;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class DefaultKeyMappings {
private final TendiwaInputProcessor defaultInputProcessor;

@Inject
DefaultKeyMappings(
	@Named("default") TendiwaInputProcessor defaultInputProcessor
) {
	this.defaultInputProcessor = defaultInputProcessor;
}
public void addMapping(InputMapping mapping, NonPointerAction action) {
	defaultInputProcessor.getMapper().addMapping(mapping, action);
}
}
