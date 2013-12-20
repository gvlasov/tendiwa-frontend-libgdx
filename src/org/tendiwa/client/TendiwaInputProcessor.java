package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import org.tendiwa.entities.CharacterAbilities;
import org.tendiwa.events.*;
import tendiwa.core.*;
import tendiwa.core.Character;
import tendiwa.core.meta.Condition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;

public abstract class TendiwaInputProcessor implements InputProcessor {
protected static final int ctrl = 1 << 8;
protected static final int alt = 1 << 9;
protected static final int shift = 1 << 10;
final GameScreen gameScreen;
final Character player;
final World world;
private Task currentTask;
private ItemToKeyMapper<Item> mapper = new ItemToKeyMapper<>();
private Map<KeyCombination, UiAction> combinationToAction = new HashMap<KeyCombination, UiAction>();

TendiwaInputProcessor(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	this.player = Tendiwa.getPlayerCharacter();
	this.world = Tendiwa.getWorld();
}

public abstract void keyDown(KeyCombination combination);

/**
 * Fills this TendiwaInputProcessor with
 */
public void putAction(int combination, UiAction action) {
	combinationToAction.put(KeyCombinationPool.obtainCombination(combination), action);
}

@Override
public boolean keyDown(int keycode) {
	if (gameScreen.isEventProcessingGoing() || Server.isTurnComputing()) {
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
	if (keycode == LEFT) {
	} else if (keycode == RIGHT) {
	} else if (keycode == UP) {
	} else if (keycode == DOWN) {
	} else if (keycode == H || keycode == NUMPAD_4) {
	} else if (keycode == L || keycode == NUMPAD_6) {
		if (player.canStepOn(player.getX() + 1, player.getY())) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.E));
		}
	} else if (keycode == J || keycode == NUMPAD_2) {
		if (player.canStepOn(player.getX(), player.getY() + 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.S));
		}
	} else if (keycode == K || keycode == NUMPAD_8) {
		if (player.canStepOn(player.getX(), player.getY() - 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.N));
		}
	} else if (keycode == Y || keycode == NUMPAD_7) {
		if (player.canStepOn(player.getX() - 1, player.getY() - 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NW));
		}
	} else if (keycode == U || keycode == NUMPAD_9) {
		if (player.canStepOn(player.getX() + 1, player.getY() - 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NE));
		}
	} else if (keycode == B || keycode == NUMPAD_1) {
		if (player.canStepOn(player.getX() - 1, player.getY() + 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SW));
		}
	} else if (keycode == N || keycode == NUMPAD_3) {
		if (player.canStepOn(player.getX() + 1, player.getY() + 1)) {
			Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SE));
		}
	} else if (keycode == A) {
		UiActions.getInstance().update();
		UiActions.getInstance().setVisible(true);
		Gdx.input.setInputProcessor(UiActions.getInstance().getInputProcessor());
	} else if (keycode == F9) {
		if (TendiwaGame.isGameScreenActive()) {
			TendiwaGame.switchToWorldMapScreen();
		} else {
			TendiwaGame.switchToGameScreen();
		}
	} else if (keycode == F10) {
		gameScreen.getConfig().toggleAnimations();
		UiLog.getInstance().pushText("Animations " + (gameScreen.getConfig().animationsEnabled ? "enabled" : "disabled") + ".");
	} else if (keycode == F11) {
		gameScreen.toggleStatusbar();
	} else if (keycode == G) {
		if (player.getPlane().hasAnyItems(player.getX(), player.getY())) {
			Tendiwa.getServer().pushRequest(new RequestPickUp());
		}
	} else if (keycode == Q && Gdx.input.isKeyPressed(SHIFT_LEFT)) {
		mapper.update(Tendiwa.getPlayerCharacter().getInventory());
		TendiwaGame.getItemSelectionScreen().startSelection(mapper, new EntityFilter<Item>() {
				@Override
				public boolean check(Item entity) {
					return entity.getType() instanceof Shootable;
				}
			}, new EntitySelectionListener<Item>() {
				@Override
				public void execute(Item item) {
					QuiveredItemHolder.setItem(item);
					TendiwaGame.switchToGameScreen();
				}
			}
		);
	} else if (keycode == F) {
		final UniqueItem rangedWeapon = (UniqueItem) player.getEquipment().getWieldedWeaponThatIs(new Condition<Item>() {
			@Override
			public boolean check(Item item) {
				return Items.isRangedWeapon(item.getType());
			}
		});
		final Item quiveredItem = QuiveredItemHolder.getItem();
		if (rangedWeapon != null && quiveredItem != null && Items.isShootable(quiveredItem.getType())) {
			final Shootable shootable = (Shootable) quiveredItem.getType();
			if (shootable.getAmmunitionType() == ((RangedWeapon) rangedWeapon.getType()).getAmmunitionType()) {
				CellSelection.getInstance().startCellSelection(new EntitySelectionListener<EnhancedPoint>() {
					@Override
					public void execute(EnhancedPoint point) {
						Tendiwa.getServer().pushRequest(new RequestShoot(rangedWeapon, quiveredItem, point.x, point.y));
					}
				});
			}
		}
	} else if (keycode == T) {
		mapper.update(Tendiwa.getPlayerCharacter().getInventory());
		TendiwaGame.getItemSelectionScreen().startSelection(mapper,
			new EntityFilter<Item>() {
				@Override
				public boolean check(Item entity) {
					return true;
				}
			},
			new EntitySelectionListener<Item>() {
				@Override
				public void execute(final Item item) {
					TendiwaGame.switchToGameScreen();
					CellSelection.getInstance().startCellSelection(new EntitySelectionListener<EnhancedPoint>() {
						@Override
						public void execute(EnhancedPoint point) {
							Tendiwa.getServer().pushRequest(new RequestThrowItem(item, point.x, point.y));
						}
					});
				}
			}
		);
	} else if (keycode == W && !Gdx.input.isKeyPressed(SHIFT_LEFT)) {
		mapper.update(Tendiwa.getPlayerCharacter().getInventory());
		TendiwaGame.getItemSelectionScreen().startSelection(mapper,
			new EntityFilter<Item>() {
				@Override
				public boolean check(Item entity) {
					return entity.getType() instanceof Wieldable;
				}
			},
			new EntitySelectionListener<Item>() {
				@Override
				public void execute(Item item) {
					Tendiwa.getServer().pushRequest(new RequestWield(item));
				}
			}
		);
	} else if (keycode == W && Gdx.input.isKeyPressed(SHIFT_LEFT)) {
		mapper.update(Tendiwa.getPlayerCharacter().getInventory());
		TendiwaGame.getItemSelectionScreen().startSelection(mapper, new EntityFilter<Item>() {
				@Override
				public boolean check(Item entity) {
					return entity.getType() instanceof Wearable;
				}
			}, new EntitySelectionListener<Item>() {
				@Override
				public void execute(Item item) {
					Tendiwa.getServer().pushRequest(new RequestPutOn((UniqueItem) item));
				}
			}
		);
	} else if (keycode == S && Gdx.input.isKeyPressed(SHIFT_LEFT)) {
		Tendiwa.getServer().pushRequest(new RequestActionWithoutTarget(
			(ActionWithoutTarget) CharacterAbilities.SHOUT.getAction()
		));
	} else if (keycode == Z) {
		UiSpells.getInstance().update();
		UiSpells.getInstance().setVisible(true);
		Gdx.input.setInputProcessor(UiSpells.getInstance().getInputProcessor());
	} else if (keycode == S) {
		Tendiwa.getServer().pushRequest(new RequestIdle());
	}
	return true;
}

@Override
public boolean keyUp(int keycode) {
	return false;
}

@Override
public boolean keyTyped(char character) {
	return false;
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	if (currentTask != null) {
		System.out.println("fuck that " + currentTask.ended());
		return false;
	}
	final int cellX = (gameScreen.startPixelX + screenX) / GameScreen.TILE_SIZE;
	final int cellY = (gameScreen.startPixelY + screenY) / GameScreen.TILE_SIZE;
	if (cellX == gameScreen.player.getX() && cellY == gameScreen.player.getY()) {
		return true;
	}
	LinkedList<EnhancedPoint> path = Paths.getPath(player.getX(), player.getY(), cellX, cellY, player, 100);
	if (path == null || path.size() == 0) {
		return true;
	}
	trySettingTask(new Task() {
		public boolean forcedEnd = false;

		@Override
		public boolean ended() {
			return forcedEnd || gameScreen.player.getX() == cellX && gameScreen.player.getY() == cellY;
		}

		@Override
		public void execute() {
			LinkedList<EnhancedPoint> path = Paths.getPath(player.getX(), player.getY(), cellX, cellY, player, 100);
			if (path == null) {
				forcedEnd = true;
				return;
			}
			if (!path.isEmpty()) {
				EnhancedPoint nextStep = path.removeFirst();
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.shiftToDirection(
					nextStep.x - player.getX(),
					nextStep.y - player.getY()
				)));
			}
		}
	});
	return true;
}

private boolean trySettingTask(Task task) {
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
		}
	}
}

static class KeyCombinationPool {
	static Map<Integer, KeyCombination> combinations = new HashMap<>();

	static KeyCombination obtainCombination(int keycode, boolean ctrl, boolean alt, boolean shift) {
		int key = keycode;
		if (ctrl) {
			key += TendiwaInputProcessor.ctrl;
		}
		if (alt) {
			key += TendiwaInputProcessor.alt;
		}
		if (shift) {
			key += TendiwaInputProcessor.shift;
		}
		if (combinations.containsKey(key)) {
			return combinations.get(key);
		} else {
			KeyCombination answer = new KeyCombination(key, ctrl, alt, shift);
			combinations.put(key, answer);
			return answer;
		}
	}

	static KeyCombination obtainCombination(int combination) {
		return obtainCombination(
			combination % (TendiwaInputProcessor.ctrl),
			(combination & TendiwaInputProcessor.ctrl) == 1,
			(combination & TendiwaInputProcessor.alt) == 1,
			(combination & TendiwaInputProcessor.shift) == 1
		);
	}
}
}

