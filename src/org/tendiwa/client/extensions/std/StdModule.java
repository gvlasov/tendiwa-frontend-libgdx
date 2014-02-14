package org.tendiwa.client.extensions.std;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.tendiwa.client.rendering.markings.MarkingsLayer;
import org.tendiwa.client.ui.input.ActionsAdder;

public class StdModule extends AbstractModule {
@Override
protected void configure() {
	bind(ActionsAdder.class)
		.annotatedWith(Names.named("default"))
		.to(StdActions.class)
		.in(Scopes.SINGLETON);
	bind(MarkingsLayer.class)
		.to(StdMarkingsLayer.class)
		.in(Scopes.SINGLETON);

}
}
