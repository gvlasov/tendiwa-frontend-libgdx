package org.tendiwa.client.ui;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class UiBaseTableProvider implements Provider<Table> {
private final Graphics graphics;

@Inject
UiBaseTableProvider(
	Graphics graphics
) {

	this.graphics = graphics;
}

@Override
public Table get() {
	Table table = new Table();
	table.setFillParent(true);
	table.setSize(graphics.getWidth(), graphics.getHeight());
	return table;
}
}
