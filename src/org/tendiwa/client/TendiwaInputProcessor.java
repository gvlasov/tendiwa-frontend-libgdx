package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.google.common.collect.ImmutableList;
import tendiwa.core.Character;
import tendiwa.core.Server;
import tendiwa.core.Tendiwa;
import tendiwa.core.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;

public abstract class TendiwaInputProcessor implements InputProcessor {
protected static final int ctrl = 1 << 8;
protected static final int alt = 1 << 9;
protected static final int shift = 1 << 10;
final GameScreen gameScreen;
final Character player;
final World world;
private Map<KeyCombination, UiAction> combinationToAction = new HashMap<>();
/**
 * Contains same data as {@link TendiwaInputProcessor#combinationToAction}, but in form of list (so it has a defined
 * order and in generally easier to iterate over).
 */
private List<Mapping> mappings = new LinkedList<>();
Task currentTask;

public TendiwaInputProcessor(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	this.player = Tendiwa.getPlayerCharacter();
	this.world = Tendiwa.getWorld();
}

public static ImmutableList<Mapping> getCurrentMappings() {
	InputProcessor mainInputProcessor = Gdx.input.getInputProcessor();
	if (mainInputProcessor == null) {
		throw new RuntimeException("No InputProcessor was set yet");
	} else if (mainInputProcessor instanceof TendiwaInputProcessor) {
		return ((TendiwaInputProcessor) mainInputProcessor).getMappings();
	} else if (mainInputProcessor instanceof InputMultiplexer) {
		for (InputProcessor processor : ((InputMultiplexer) mainInputProcessor).getProcessors()) {
			if (processor instanceof TendiwaInputProcessor) {
				return ((TendiwaInputProcessor) processor).getMappings();
			}
		}
		throw new RuntimeException("Can't get current mappings");
	} else {
		throw new RuntimeException("Can't get current mappings");
	}
}

/**
 * Maps a key combination to an action so action will be executed when that key combination is pressed.
 *
 * @param combination
 * 	An integer which is a sum of 4 parameters: <ul><li>Keycode (from {@link Input.Keys})</li><li>{@code isCtrl ? 1 << 8
 * 	: 0}</li><li>{@code isAlt ? 1 << 9 : 0}</li><li>{@code isShift ? 1 << 10 : 0}</li></ul>
 */
public void putAction(int combination, UiAction action) {
	Mapping mapping = new Mapping(KeyCombinationPool.obtainCombination(combination), action);
	combinationToAction.put(mapping.getCombination(), mapping.getAction());
	mappings.add(mapping);
}

@Override
public boolean keyDown(int keycode) {
//	System.out.println(gameScreen.isEventProcessingGoing()+" "+Server.isTurnComputing());
	if (keycode == ESCAPE && currentTask != null) {
		System.out.println("undo");
		currentTask = null;
	}
	if (gameScreen.isEventProcessingGoing() || Server.isTurnComputing()) {
		return false;
	}
	switch (keycode) {
		case SHIFT_LEFT:
		case SHIFT_RIGHT:
		case ALT_LEFT:
		case ALT_RIGHT:
		case CONTROL_LEFT:
		case CONTROL_RIGHT:
			return false;
	}
	KeyCombination combination = KeyCombinationPool.obtainCombination(
		keycode,
		Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT),
		Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT),
		Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
	);
	UiAction action = combinationToAction.get(combination);
	if (action == null) {
		return false;
	} else {
		action.act();
		return true;
	}
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	return false;
}

boolean trySettingTask(Task task) {
	if (currentTask == null) {
		currentTask = task;
		return true;
	} else {
		return false;
	}
}

@Override
public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	return false;
}

@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
	return false;
}

@Override
public boolean mouseMoved(int screenX, int screenY) {
	return false;
}

@Override
public boolean scrolled(int amount) {
	return false;
}

public void executeCurrentTask() {
	if (currentTask != null) {
		if (currentTask.ended()) {
			currentTask = null;
		} else {
			currentTask.execute();
			if (Tendiwa.getPlayerCharacter().isUnderAnyThreat()) {
				currentTask = null;
			}
		}
	}
}

public ImmutableList<Mapping> getMappings() {
	return ImmutableList.copyOf(mappings);
}

private static class KeyCombinationPool {
	static Map<Integer, KeyCombination> combinations = new HashMap<>();

	static KeyCombination obtainCombination(int keycode, boolean ctrl, boolean alt, boolean shift) {
		int compositeKeyCode = computeCompositeKeyCode(keycode, ctrl, alt, shift);
		if (combinations.containsKey(compositeKeyCode)) {
			return combinations.get(compositeKeyCode);
		} else {
			KeyCombination answer = new KeyCombination(keycode, ctrl, alt, shift);
			combinations.put(compositeKeyCode, answer);
			return answer;
		}
	}

	static int computeCompositeKeyCode(int keycode, boolean ctrl, boolean alt, boolean shift) {
		return keycode
			+ (ctrl ? TendiwaInputProcessor.ctrl : 0)
			+ (alt ? TendiwaInputProcessor.alt : 0)
			+ (shift ? TendiwaInputProcessor.shift : 0);
	}

	static KeyCombination obtainCombination(int combination) {
		return obtainCombination(
			combination % (TendiwaInputProcessor.ctrl),
			(combination & TendiwaInputProcessor.ctrl) == TendiwaInputProcessor.ctrl,
			(combination & TendiwaInputProcessor.alt) == TendiwaInputProcessor.alt,
			(combination & TendiwaInputProcessor.shift) == TendiwaInputProcessor.shift
		);
	}
}

}

