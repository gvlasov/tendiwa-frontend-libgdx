package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import org.tendiwa.events.RequestActionToCell;
import org.tendiwa.events.RequestActionWithoutTarget;
import tendiwa.core.*;

import java.util.Map;

public class UiActions extends TendiwaWidget {
private static UiActions INSTANCE;
private static Runnable onComplete = new Runnable() {
	@Override
	public void run() {
		INSTANCE.setVisible(false);
		Gdx.input.setInputProcessor(TendiwaGame.getGameScreen().getInputProcessor());
	}
};
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<CharacterAbility> mapper = new ItemToKeyMapper<>();
private EntitySelectionListener<CharacterAbility> onActionSelected = new EntitySelectionListener<CharacterAbility>() {
	@Override
	public void execute(final CharacterAbility characterAbility) {
		final ActionTargetType action = characterAbility.getAction();
		if (action instanceof ActionToCell) {
			CellSelection.getInstance().startCellSelection(new EntitySelectionListener<EnhancedPoint>() {
				@Override
				public void execute(EnhancedPoint point) {
					Tendiwa.getServer().pushRequest(new RequestActionToCell(
						(ActionToCell) action,
						point.x,
						point.y
					));
				}
			});
		} else if (action instanceof ActionWithoutTarget) {
			Tendiwa.getServer().pushRequest(
				new RequestActionWithoutTarget(
					(ActionWithoutTarget) action
				)
			);
		}
	}
};
private Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.WHITE);

private UiActions() {
	super();
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
	for (Map.Entry<CharacterAbility, java.lang.Character> e : mapper) {
		flowGroup.addActor(createActionWidget(e.getKey(), e.getValue()));
	}
}

private WidgetGroup createActionWidget(CharacterAbility action, char character) {
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
	return new ItemSelectionInputProcessor<>(mapper, onActionSelected, onComplete);
}
}
