package org.tendiwa.client.extensions.std.spells;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.core.Spell;

public class SpellsWidgetModule extends AbstractModule {
@Override
protected void configure() {
}
@Provides
@Singleton
@Named("spells_widget")
public ItemToKeyMapper<Spell> provideMapper() {
	return new ItemToKeyMapper<>();
}
}
