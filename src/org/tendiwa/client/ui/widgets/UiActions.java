package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.*;
import org.tendiwa.client.ui.controller.ActionSelectionListener;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.client.ui.input.KeyboardAction;
import org.tendiwa.core.Character;
import org.tendiwa.core.*;

import java.util.Collection;
import java.util.Map;

public class UiActions extends TendiwaWidget {
private final Character player;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<CharacterAbility> mapper = new ItemToKeyMapper<>();
private Label.LabelStyle style;

@Inject
public UiActions(
	Character player,
	InputToActionMapper actionMapper,
	final ActionSelectionListener onActionSelected,
	FontRegistry fontRegistry,
	ColorFillFactory colorFillFactory,
	@Named("default") final InputProcessor defaultInputProcessor,
	final Input gdxInput
) {
	super();
	this.player = player;
	style = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
	actionMapper.putAction(Input.Keys.A, new KeyboardAction("action.actionsMenu") {
		@Override
		public void act() {
			setVisible(true);
			gdxInput.setInputProcessor(new EntitySelectionInputProcessor<>(mapper, onActionSelected, new Runnable() {
				@Override
				public void run() {
					setVisible(false);
					gdxInput.setInputProcessor(defaultInputProcessor);
				}
			}));
		}
	});
}

/**
 * Finds available actions on objects around player character.
 *
 * @return
 */
public Iterable<CharacterAbility> findActionsAroundPlayer() {
	HorizontalPlane plane = player.getPlane();
	ImmutableSet.Builder<CharacterAbility> builder = ImmutableSet.builder();
	for (Direction dir : Directions.ALL_DIRECTIONS) {
		int[] d = dir.side2d();
		int x = player.getX() + d[0];
		int y = player.getY() + d[1];
		if (plane.containsCell(x, y) && plane.hasObject(x, y)) {
			Usable usable = GameObjects.asUsable(plane.getGameObject(x, y).getType());
			if (usable != null) {
				builder.addAll(usable.getActions());
			}
		}
	}
	return builder.build();
}

public void update() {
	Iterable<CharacterAbility> actionsAroundPlayer = findActionsAroundPlayer();
	Collection<CharacterAbility> characterActions = player.getAvailableActions();
	mapper.update(Iterables.concat(actionsAroundPlayer, characterActions));
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

}
