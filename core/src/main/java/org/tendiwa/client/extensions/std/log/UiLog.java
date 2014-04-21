package org.tendiwa.client.extensions.std.log;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.client.extensions.std.DefaultSkin;
import org.tendiwa.client.HorizontalFlowGroup;
import org.tendiwa.client.ui.TendiwaWidget;
import org.tendiwa.client.ui.factories.ColorFillFactory;
import org.tendiwa.client.ui.fonts.FontRegistry;
import org.tendiwa.client.ui.model.EventLogMessage;
import org.tendiwa.client.ui.model.MessageLog;
import org.tendiwa.core.observation.Finishable;
import org.tendiwa.core.observation.Observer;

import java.util.LinkedList;
import java.util.List;

public class UiLog extends TendiwaWidget {

private Label.LabelStyle style;
private List<String> messages = new LinkedList<>();
private HorizontalFlowGroup flowGroup = new HorizontalFlowGroup();

@Inject
public UiLog(
	FontRegistry fontRegistry,
	ColorFillFactory colorFillFactory,
	MessageLog model,
    @Named("default") Skin skin
) {
	super();
	setBackground(colorFillFactory.create(Color.DARK_GRAY).getDrawable());
	final ScrollPane scrollPane = new ScrollPane(flowGroup, skin);
	style = new Label.LabelStyle(fontRegistry.obtain(14, false), Color.WHITE);
	scrollPane.setScrollingDisabled(true, false);
	scrollPane.setSmoothScrolling(false);
	add(scrollPane).expand().fill();
	model.subscribe(new Observer<EventLogMessage>() {
		@Override
		public void update(EventLogMessage event, Finishable<EventLogMessage> emitter) {
			pushText(event.message);
			emitter.done(this);
		}
	}, EventLogMessage.class);
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
