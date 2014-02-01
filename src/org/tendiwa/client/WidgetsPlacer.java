package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Creates TendiwaWidgets and places them to {@link TendiwaUiStage}'s {@link com.badlogic.gdx.scenes.scene2d.ui.Table}.
 */
interface WidgetsPlacer {
public void placeWidgets(Stage stage, Table table);
}
