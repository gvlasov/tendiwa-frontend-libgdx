package org.tendiwa.client.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Creates TendiwaWidgets and places them to {@link org.tendiwa.client.ui.TendiwaUiStage}'s {@link com.badlogic.gdx.scenes.scene2d.ui.Table}.
 */
public interface WidgetsPlacer {
public void placeWidgets(Stage stage, Table table);
}
