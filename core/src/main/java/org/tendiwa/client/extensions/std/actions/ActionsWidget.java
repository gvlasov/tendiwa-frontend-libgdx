package org.tendiwa.client.extensions.std.actions;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.client.AtlasItems;
import org.tendiwa.client.ItemToKeyMapper;
import org.tendiwa.client.ui.TendiwaWidget;
import org.tendiwa.client.VerticalFlowGroup;
import org.tendiwa.core.Character;
import org.tendiwa.core.*;

import java.util.Collection;
import java.util.Map;

@Singleton
public class ActionsWidget extends TendiwaWidget {
private final Character player;
private final VerticalFlowGroup flowGroup = new VerticalFlowGroup();
private final ItemToKeyMapper<CharacterAbility> mapper;
private final Label.LabelStyle style;

@Inject
public ActionsWidget(
	@Named("player") Character player,
	@Named("actions_widget") ItemToKeyMapper<CharacterAbility> mapper,
	@Named("actions_widget") Label.LabelStyle style,
	@Named("actions_widget") Drawable background
) {
	super();
	this.mapper = mapper;
	this.player = player;
	this.style = style;
	setBackground(background);
	add(flowGroup).expand().fill();
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
		int x = player.x() + d[0];
		int y = player.y() + d[1];
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
