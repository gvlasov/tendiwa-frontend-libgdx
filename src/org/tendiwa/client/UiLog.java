package org.tendiwa.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import java.util.LinkedList;
import java.util.List;

public class UiLog extends TendiwaWidget {

private static UiLog INSTANCE;
private static Label.LabelStyle style = new Label.LabelStyle(TendiwaFonts.default14NonFlipped, Color.WHITE);
private List<String> messages = new LinkedList<>();
private HorizontalFlowGroup flowGroup = new HorizontalFlowGroup();

public UiLog() {
	super();
	setBackground(TendiwaUiStage.createImage(Color.DARK_GRAY).getDrawable());
	final ScrollPane scrollPane = new ScrollPane(flowGroup, DefaultSkin.getInstance());
	scrollPane.setScrollingDisabled(true, false);
	scrollPane.setSmoothScrolling(false);
	add(scrollPane).expand().fill();
}

public static UiLog getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new UiLog();
	}
	return INSTANCE;
}

@Override
public void update() {
}

public void pushText(String text) {
	messages.add(text);
	Label label = new Label(text+" ", style);
	flowGroup.addActor(label);
	flowGroup.layout();
}

public void clear() {
	clearChildren();
}
}
