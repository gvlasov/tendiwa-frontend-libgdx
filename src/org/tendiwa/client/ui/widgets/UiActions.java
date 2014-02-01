package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import org.tendiwa.client.*;
import org.tendiwa.client.ui.controller.ActionSelectionListener;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.core.Character;
import org.tendiwa.core.*;

import java.util.Collection;
import java.util.Map;

public class UiActions extends TendiwaWidget {
private final GameScreen gameScreen;
private final EntitySelectionListener<CharacterAbility> onActionSelected;
VerticalFlowGroup flowGroup = new VerticalFlowGroup();
ItemToKeyMapper<CharacterAbility> mapper = new ItemToKeyMapper<>();
private Runnable onComplete = new Runnable() {
	@Override
	public void run() {
		UiActions.this.setVisible(false);
		Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
	}
};
private Label.LabelStyle style;
private EntitySelectionInputProcessor<CharacterAbility> actionInputProcessor;

@Inject
public UiActions(TendiwaInputProcessor tendiwaInputProcessor, final ActionSelectionListener onActionSelected, GameScreen gameScreen, FontRegistry fontRegistry, ColorFillFactory colorFillFactory) {
	super(UiPortion.ACTIONS);
	this.onActionSelected = onActionSelected;
	this.gameScreen = gameScreen;
	style = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	setBackground(colorFillFactory.create(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	add(flowGroup).expand().fill();
	tendiwaInputProcessor.putAction(Input.Keys.A, new UiAction("action.actionsMenu") {
		@Override
		public void act() {
			UiActions.this.setVisible(true);
			Gdx.input.setInputProcessor(new EntitySelectionInputProcessor<>(mapper, onActionSelected, onComplete));
		}
	});
}

/**
 * Finds available actions on objects around player character.
 *
 * @return
 */
public static Iterable<CharacterAbility> findActionsAroundPlayer() {
	Character player = Tendiwa.getPlayerCharacter();
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
	Collection<CharacterAbility> characterActions = Tendiwa.getPlayerCharacter().getAvailableActions();
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

@Override
public void onShow() {
}
}
