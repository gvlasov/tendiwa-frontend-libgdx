package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import org.tendiwa.entities.CharacterAbilities;
import org.tendiwa.events.*;
import tendiwa.core.*;
import tendiwa.core.Character;
import tendiwa.core.meta.Condition;

import java.util.LinkedList;

import static com.badlogic.gdx.Input.Keys.*;

public class GameScreenInputProcessor extends TendiwaInputProcessor {

private ItemToKeyMapper<Item> mapper = new ItemToKeyMapper<>();

public GameScreenInputProcessor(final GameScreen gameScreen) {
	super(gameScreen);
	putAction(LEFT, new UiAction("action.cameraMoveWest") {
		@Override
		public void act() {
			if (gameScreen.startCellX > gameScreen.cameraMoveStep - 1) {
				gameScreen.centerCamera(gameScreen.centerPixelX - GameScreen.TILE_SIZE, gameScreen.centerPixelY);
			}
		}
	});
	putAction(RIGHT, new UiAction("action.cameraMoveEast") {
		@Override
		public void act() {
			if (gameScreen.startCellX < gameScreen.maxStartX) {
				gameScreen.centerCamera(gameScreen.centerPixelX + GameScreen.TILE_SIZE, gameScreen.centerPixelY);
			}
		}
	});
	putAction(UP, new UiAction("action.cameraMoveNorth") {
		@Override
		public void act() {
			if (gameScreen.startCellY > gameScreen.cameraMoveStep - 1) {
				gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY - GameScreen.TILE_SIZE);
			}
		}
	});
	putAction(DOWN, new UiAction("action.cameraMoveSouth") {
		@Override
		public void act() {
			if (gameScreen.startCellY < gameScreen.maxStartY) {
				gameScreen.centerCamera(gameScreen.centerPixelX, gameScreen.centerPixelY + GameScreen.TILE_SIZE);
			}
		}
	});
	putAction(H, new UiAction("action.stepWest") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX() - 1, player.getY());
		}
	});
	putAction(L, new UiAction("action.stepEast") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX() + 1, player.getY());
		}
	});
	putAction(J, new UiAction("action.stepSouth") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX(), player.getY() + 1);
		}

	});
	putAction(K, new UiAction("action.stepNorth") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX(), player.getY() - 1);
		}

	});
	putAction(Y, new UiAction("action.stepNorthWest") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX() - 1, player.getY() - 1);
		}
	});
	putAction(U, new UiAction("action.stepNorthEast") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX() + 1, player.getY() - 1);
		}
	});
	putAction(B, new UiAction("action.stepSouthWest") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX() - 1, player.getY() + 1);
		}
	});
	putAction(N, new UiAction("action.stepSouthEast") {
		@Override
		public void act() {
			moveToOrAttackCharacterInCell(player.getX() + 1, player.getY() + 1);
		}
	});
	putAction(A, new UiAction("action.actionsMenu") {
		@Override
		public void act() {
			UiActions.getInstance().update();
			UiActions.getInstance().setVisible(true);
			Gdx.input.setInputProcessor(UiActions.getInstance().getInputProcessor());
		}
	});
	putAction(F9, new UiAction("action.toggleWorldMapScreen") {
		@Override
		public void act() {
			if (TendiwaGame.isGameScreenActive()) {
				TendiwaGame.switchToWorldMapScreen();
			} else {
				TendiwaGame.switchToGameScreen();
			}
		}
	});
	putAction(F10, new UiAction("action.toggleAnimations") {
		@Override
		public void act() {
			gameScreen.getConfig().toggleAnimations();
			UiLog.getInstance().pushText("Animations " + (gameScreen.getConfig().animationsEnabled ? "enabled" : "disabled") + ".");
		}
	});
	putAction(F11, new UiAction("action.toggleStatusBar") {
		@Override
		public void act() {
			gameScreen.toggleStatusbar();
		}
	});
	putAction(G, new UiAction("action.pickUp") {
		@Override
		public void act() {
			if (player.getPlane().hasAnyItems(player.getX(), player.getY())) {
				Tendiwa.getServer().pushRequest(new RequestPickUp());
			}
		}
	});
	putAction(shift + Q, new UiAction("action.quiver") {
		@Override
		public void act() {
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
		}
	});
	putAction(T, new UiAction("action.throw") {
		@Override
		public void act() {
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
						CellSelection.getInstance().start(new EntitySelectionListener<EnhancedPoint>() {
							@Override
							public void execute(EnhancedPoint point) {
								Tendiwa.getServer().pushRequest(new RequestThrowItem(item, point.x, point.y));
							}
						});
					}
				}
			);
		}
	});
	putAction(F, new UiAction("action.fire") {
		@Override
		public void act() {
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
					CellSelection.getInstance().start(new EntitySelectionListener<EnhancedPoint>() {
						@Override
						public void execute(EnhancedPoint point) {
							Tendiwa.getServer().pushRequest(new RequestShoot(rangedWeapon, quiveredItem, point.x, point.y));
						}
					});
				}
			}
		}
	});
	putAction(W, new UiAction("action.wield") {
		@Override
		public void act() {
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
		}
	});
	putAction(shift + W, new UiAction("action.wear") {
		@Override
		public void act() {
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
		}
	});
	putAction(shift + S, new UiAction("action.shout") {
		@Override
		public void act() {
			Tendiwa.getServer().pushRequest(new RequestActionWithoutTarget(
				(ActionWithoutTarget) CharacterAbilities.SHOUT.getAction()
			));
		}
	});
	putAction(Z, new UiAction("action.castMenu") {
		@Override
		public void act() {
			UiSpells.getInstance().update();
			UiSpells.getInstance().setVisible(true);
			Gdx.input.setInputProcessor(UiSpells.getInstance().getInputProcessor());
		}
	});
	putAction(S, new UiAction("action.idle") {
		@Override
		public void act() {
			Tendiwa.getServer().pushRequest(new RequestIdle());
		}
	});
	putAction(shift + SLASH, new UiAction("action.key_hints") {
		@Override
		public void act() {
			UiKeyHints.getInstance().update();
			UiKeyHints.getInstance().setVisible(true);
			Gdx.input.setInputProcessor(UiKeyHints.getInstance().getInputProcessor());
		}
	});
}

@Override
public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	if (currentTask != null) {
		return false;
	}
	final int cellX = (gameScreen.startPixelX + screenX) / GameScreen.TILE_SIZE;
	final int cellY = (gameScreen.startPixelY + screenY) / GameScreen.TILE_SIZE;
	if (cellX == gameScreen.player.getX() && cellY == gameScreen.player.getY()) {
		return true;
	}
	LinkedList<EnhancedPoint> path = Paths.getPath(
		player.getX(), player.getY(),
		cellX, cellY,
		player.getPathWalkerOverCharacters(),
		100
	);
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
			LinkedList<EnhancedPoint> path = Paths.getPath(
				player.getX(), player.getY(),
				cellX, cellY,
				player.getPathWalkerOverCharacters(),
				100
			);
			if (path == null) {
				forcedEnd = true;
				return;
			}
			if (!path.isEmpty()) {
				EnhancedPoint nextStep = path.removeFirst();
				moveToOrAttackCharacterInCell(nextStep.x, nextStep.y);
//				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.shiftToDirection(
//					nextStep.x - player.getX(),
//					nextStep.y - player.getY()
//				)));
			}
		}
	});
	return true;
}

/**
 * Is there is an enemy in a cell x:y, then this method will push a request to attack that enemy, otherwise this method
 * will push a request to move to that cell.
 *
 * @param x
 * 	X coordinate in world coordinates of a cell to move-attack to.
 * @param y
 * 	Y coordinate in world coordinates of a cell to move-attack to.
 * @return true if player attacked someone in that cell, false if it moved to that cell.
 */
private void moveToOrAttackCharacterInCell(int x, int y) {
	// Only neighbor cells are allowed here
	int dx = player.getX() - x;
	int dy = player.getY() - y;
	assert Math.abs(dx) <= 1 && Math.abs(dy) <= 1;
	if (player.canStepOn(x, y)) {
		Tendiwa.getServer().pushRequest(new RequestWalk(Directions.shiftToDirection(-dx, -dy)));
	} else {
		Character aim = gameScreen.backendWorld.getDefaultPlane().getCharacter(x, y);
		boolean isCharacterPresent = aim != null;
		if (isCharacterPresent) {
			boolean isHeAnEnemy = aim.isEnemy(player);
			if (isHeAnEnemy) {
				Tendiwa.getServer().pushRequest(new RequestAttack(aim));
			} else {
				assert false : "This should not happen";
			}
		}
	}
}
}
