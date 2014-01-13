package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.tendiwa.core.Chunk;
import org.tendiwa.core.EnhancedPoint;
import org.tendiwa.core.meta.Coordinate;

public class CellSelection {
private static CellSelection INSTANCE;
private final GameScreen gameScreen;
private InputProcessor cellSelectionInputProcessor = new InputProcessor() {
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE) {
			disable();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (character == 'h') {
			moveCursorBy(-1, 0);
		} else if (character == 'j') {
			moveCursorBy(0, 1);
		} else if (character == 'k') {
			moveCursorBy(0, -1);
		} else if (character == 'l') {
			moveCursorBy(1, 0);
		} else if (character == 'y') {
			moveCursorBy(-1, -1);
		} else if (character == 'u') {
			moveCursorBy(1, -1);
		} else if (character == 'b') {
			moveCursorBy(-1, 1);
		} else if (character == 'n') {
			moveCursorBy(1, 1);
		} else if (character == 'f' || character == ' ') {
			selectCurrentCell();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		selectCurrentCell();
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		EnhancedPoint point = gameScreen.screenPixelToWorldCell(screenX, screenY);
		x = point.x;
		y = point.y;
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
};
private EntitySelectionListener<EnhancedPoint> entitySelectionListener;
private Texture texture;
private int x = 0;
private int y = 0;
private boolean isActive = false;

public CellSelection(GameScreen gameScreen) {
	this.gameScreen = gameScreen;
	buildTexture();
}

public static CellSelection getInstance() {
	if (INSTANCE == null) {
		INSTANCE = new CellSelection(TendiwaGame.getGameScreen());
	}
	return INSTANCE;
}

/**
 * Passes a cell that is currently under cursor to the listener that was assigned at {@link
 * CellSelection#start(EntitySelectionListener)}
 */
private void selectCurrentCell() {
	entitySelectionListener.execute(new EnhancedPoint(x, y));
	disable();
}

/**
 * Disables cell selection without actually selecting any cell. Sets current InputProcessor back to default {@link
 * org.tendiwa.client.GameScreen#inputMultiplexer}.
 */
private void disable() {
	entitySelectionListener = null;
	isActive = false;
	Gdx.input.setInputProcessor(TendiwaGame.getGameScreen().getInputProcessor());
}

private void moveCursorBy(int dx, int dy) {
	x += dx;
	y += dy;
}

private void moveCursorTo(int x, int y) {
	this.x = x;
	this.y = y;
}

/**
 * Enters cell selection mode, changes current InputProcessor to cell selection mode processor, and adds a listener that
 * will wait for a cell to be selected with {@link org.tendiwa.client.CellSelection#selectCurrentCell()}.
 *
 * @param entitySelectionListener
 * 	A listener that will wait for a cell to be selected.
 */
void start(EntitySelectionListener<EnhancedPoint> entitySelectionListener) {
	isActive = true;
	this.entitySelectionListener = entitySelectionListener;
	Gdx.input.setInputProcessor(cellSelectionInputProcessor);
	moveCursorTo(gameScreen.getCursor().getWorldX(), gameScreen.getCursor().getWorldY());
}

boolean isActive() {
	return isActive;
}

void draw() {
	if (!isActive) {
		throw new RuntimeException("CellCollection can't be drawn because it is not active");
	}
	gameScreen.batch.begin();
	Coordinate[] vector = Chunk.vector(gameScreen.player.getX(), gameScreen.player.getY(), x, y);
	for (Coordinate coord : vector) {
		gameScreen.batch.draw(texture, coord.x * GameScreen.TILE_SIZE, coord.y * GameScreen.TILE_SIZE);
	}
	gameScreen.batch.end();
}

void buildTexture() {
	Pixmap pixmap = new Pixmap(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE, Pixmap.Format.RGBA8888);
	pixmap.setColor(0, 1, 0, 0.3f);
	pixmap.fillRectangle(0, 0, GameScreen.TILE_SIZE - 1, GameScreen.TILE_SIZE - 1);
	texture = new Texture(pixmap);
}
Texture getTexture() {
	return texture;
}
}
