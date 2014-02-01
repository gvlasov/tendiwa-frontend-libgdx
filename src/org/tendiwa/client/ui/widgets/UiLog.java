package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import org.tendiwa.client.DefaultSkin;
import org.tendiwa.client.HorizontalFlowGroup;
import org.tendiwa.client.TendiwaWidget;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;

import java.util.LinkedList;
import java.util.List;

public class UiLog extends TendiwaWidget {

private static UiLog INSTANCE;
private Label.LabelStyle style;
private List<String> messages = new LinkedList<>();
private HorizontalFlowGroup flowGroup = new HorizontalFlowGroup();

public UiLog(FontRegistry fontRegistry, ColorFillFactory colorFillFactory) {
	super();
	setBackground(colorFillFactory.create(Color.DARK_GRAY).getDrawable());
	final ScrollPane scrollPane = new ScrollPane(flowGroup, DefaultSkin.getInstance());
	style = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	scrollPane.setScrollingDisabled(true, false);
	scrollPane.setSmoothScrolling(false);
	add(scrollPane).expand().fill();
}


public void pushText(String text) {
	messages.add(text);
	Label label = new Label(text + " ", style);
	flowGroup.addActor(label);
	flowGroup.layout();
}

//@Override
//public void clear() {
//	clearChildren();
//}
}
