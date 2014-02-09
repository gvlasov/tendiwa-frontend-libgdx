package org.tendiwa.client.ui.uiModes;

import com.badlogic.gdx.InputProcessor;
import org.tendiwa.client.ui.input.ActionsAdder;
import org.tendiwa.client.ui.input.InputToActionMapper;

public interface UiMode extends InputProcessor {
public InputToActionMapper getMapper();

public void addMappings(ActionsAdder adder);
}
