package org.tendiwa.client.ui.cellSelection;

import com.badlogic.gdx.Input;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.client.GameScreenViewport;
import org.tendiwa.client.ui.input.ActionsAdder;
import org.tendiwa.client.ui.input.InputToActionMapper;
import org.tendiwa.client.ui.input.KeyboardAction;
import org.tendiwa.client.ui.input.MouseAction;
import org.tendiwa.client.ui.model.CursorPosition;
import org.tendiwa.client.ui.uiModes.UiModeManager;
import org.tendiwa.core.Cell;

import static com.badlogic.gdx.Input.Keys.*;

@Singleton
class CellSelectionActionAdder implements ActionsAdder {
private final UiModeManager uiModeManager;
private final CursorPosition cursorPosition;
private final GameScreenViewport viewport;
private CellSelection currentSelection;

@Inject
CellSelectionActionAdder(
	UiModeManager uiModeManager,
	CursorPosition cursorPosition,
	GameScreenViewport viewport
) {
	super();
	this.uiModeManager = uiModeManager;
	this.cursorPosition = cursorPosition;
	this.viewport = viewport;
}

/**
 * Having this method is bad practice for dependency injection point of view, but it is done so to allow only one
 * instance of UiMode for cell selection to exist, without recreating almost the same UiMode each time a CellSelection
 * is started. However, this is localized in cell selection module, so it doesn't contaminate API.
 *
 * @param selection
 */
void setCurrentSelection(CellSelection selection) {
	currentSelection = selection;
}

@Override
public void addTo(InputToActionMapper mapper) {
	mapper.putMouseAction(Input.Buttons.LEFT, new MouseAction("ui.actions.mouse") {
		@Override
		public void act(int clickPixelX, int clickPixelY) {
			uiModeManager.popMode();
			currentSelection.selectCurrentCell();
			currentSelection = null;
		}
	});
	KeyboardAction select = new

		KeyboardAction("ui.actions.cell_selection.select") {
			@Override
			public void act() {
				uiModeManager.popMode();
				currentSelection.selectCurrentCell();
				currentSelection = null;
			}
		};
	mapper.putAction(F, select);
	mapper.putAction(SPACE, select);
	mapper.putMouseMovedAction(new MouseAction("ui.actions.cell_selection.mouse_moved") {
		@Override
		public void act(int screenX, int screenY) {
			Cell point = viewport.screenPixelToWorldCell(screenX, screenY);
			cursorPosition.setPoint(point);
		}
	});
	mapper.putAction(ESCAPE, new KeyboardAction("ui.actions.cell_selection.abort") {
		@Override
		public void act() {
			uiModeManager.popMode();
			currentSelection.exit();
			currentSelection = null;
		}
	});
	mapper.putAction(H, new KeyboardAction("ui.actions.cell_selection.west") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(-1, 0);
		}
	});
	mapper.putAction(J, new KeyboardAction("ui.actions.cell_selection.south") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(0, 1);
		}
	});
	mapper.putAction(K, new KeyboardAction("ui.actions.cell_selection.north") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(0, -1);
		}
	});
	mapper.putAction(L, new KeyboardAction("ui.actions.cell_selection.east") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(1, 0);
		}
	});
	mapper.putAction(Y, new KeyboardAction("ui.actions.cell_selection.north_west") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(-1, -1);
		}
	});
	mapper.putAction(U, new KeyboardAction("ui.actions.cell_selection.north_east") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(1, -1);
		}
	});
	mapper.putAction(B, new KeyboardAction("ui.actions.cell_selection.south_west") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(-1, 1);
		}
	});
	mapper.putAction(N, new KeyboardAction("ui.actions.cell_selection.south_east") {
		@Override
		public void act() {
			cursorPosition.moveCursorBy(1, 1);
		}
	});
}
}
