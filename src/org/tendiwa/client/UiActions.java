package org.tendiwa.client;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import tendiwa.core.CharacterAction;
import tendiwa.core.Tendiwa;

import java.util.Map;

public class UiActions extends TendiwaWidget {
private static UiActions INSTANCE;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<CharacterAction> mapper = new ItemToKeyMapper<>();
private Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.WHITE);
private ItemSelectionInputProcessor<CharacterAction> inputProcessor = null;

private UiActions() {
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
}

public static UiActions getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new UiActions();
	}
	return INSTANCE;
}

@Override
public void update() {
	mapper.update(Tendiwa.getPlayerCharacter().getAvailableActions());
	flowGroup.clearChildren();
	for (Map.Entry<CharacterAction, Character> e : mapper)
		flowGroup.addActor(createActionWidget(e.getKey(), e.getValue()));
}

private WidgetGroup createActionWidget(CharacterAction action, char character) {
	Label actionNameLabel = new Label(action.getResourceName(), style);
	Label characterLabel = new Label(String.valueOf(character), style);
	Image image = new Image(AtlasItems.getInstance().findRegion("short_bow"));
	Table table = new Table();
	table.add(image).padRight(10);
	table.add(characterLabel).padRight(20);
	table.add(actionNameLabel);
	table.pad(4);
	return table;
}

public InputProcessor getInputProcessor() {
	return inputProcessor;
}
}
