package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import org.tendiwa.entities.CharacterAbilities;
import org.tendiwa.events.*;
import tendiwa.core.*;
import tendiwa.core.meta.Condition;

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
			if (player.canStepOn(player.getX() - 1, player.getY())) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.W));
			}
		}
	});
	putAction(L, new UiAction("action.stepEast") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX() + 1, player.getY())) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.E));
			}
		}
	});
	putAction(J, new UiAction("action.stepSouth") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX(), player.getY() + 1)) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.S));
			}
		}

	});
	putAction(K, new UiAction("action.stepNorth") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX(), player.getY() - 1)) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.N));
			}
		}

	});
	putAction(Y, new UiAction("action.stepNorthWest") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX() - 1, player.getY() - 1)) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NW));
			}
		}
	});
	putAction(U, new UiAction("action.stepNorthEast") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX() + 1, player.getY() - 1)) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.NE));
			}
		}
	});
	putAction(B, new UiAction("action.stepSouthWest") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX() - 1, player.getY() + 1)) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SW));
			}
		}
	});
	putAction(N, new UiAction("action.stepSouthEast") {
		@Override
		public void act() {
			if (player.canStepOn(player.getX() + 1, player.getY() + 1)) {
				Tendiwa.getServer().pushRequest(new RequestWalk(Directions.SE));
			}
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
						CellSelection.getInstance().startCellSelection(new EntitySelectionListener<EnhancedPoint>() {
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
					CellSelection.getInstance().startCellSelection(new EntitySelectionListener<EnhancedPoint>() {
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
public void keyDown(KeyCombination combination) {

}
}
