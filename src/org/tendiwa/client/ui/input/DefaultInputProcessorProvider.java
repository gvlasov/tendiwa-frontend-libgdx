package org.tendiwa.client.ui.input;

import com.badlogic.gdx.Input;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class DefaultInputProcessorProvider implements Provider<TendiwaInputProcessor> {
private final TendiwaInputProcessorFactory factory;
private final Input gdxInput;

@Inject
DefaultInputProcessorProvider(
	TendiwaInputProcessorFactory factory,
    Input gdxInput
) {
	this.factory = factory;
	this.gdxInput = gdxInput;
}
@Override
public TendiwaInputProcessor get() {
	return factory.create(new InputToActionMapper(gdxInput));
}
}
