package org.tendiwa.client.extensions.std.actions;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.ui.uiModes.UiMode;
import org.tendiwa.core.CharacterAbility;

public class ActionsWidgetModule extends AbstractModule {
@Override
protected void configure() {
	bind(Drawable.class)
		.annotatedWith(Names.named("actions_widget"))
		.toProvider(BackgroundDrawableProvider.class)
		.in(Scopes.SINGLETON);
	bind(Label.LabelStyle.class)
		.annotatedWith(Names.named("actions_widget"))
		.toProvider(LabelStyleProvider.class)
		.in(Scopes.SINGLETON);
	bind(UiMode.class)
		.annotatedWith(Names.named("actions"))
		.toProvider(ActionsUiModeProvider.class);
}
@Provides
@Singleton
@Named("actions_widget")
public ItemToKeyMapper<CharacterAbility> provideMapper() {
	return new ItemToKeyMapper<>();
}
}
