package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import tendiwa.core.*;
import tendiwa.core.meta.Chance;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

private final TendiwaGame game;
private final PixmapTextureAtlas pixmapTextureAtlas;
private final TextureAtlas atlas;
private final SpriteBatch batch;
private final Cell[][] cells;
private final ShaderProgram shader;
private final int windowHeight;
private final int windowWidth;
private final HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Texture>>>> floorTextures = new HashMap<>();
private final Map<CardinalDirection, Map<Integer, Pixmap>> floorTransitions = new HashMap<>();
private final int TILE_SIZE = 32;
private final Texture cursor;
private final int windowWidthCells;
private final int windowHeightCells;
private final int maxStartX;
private final int maxStartY;
OrthographicCamera camera;
String vertexShader = "attribute vec4 a_position;    \n" +
	"attribute vec4 a_color;\n" +
	"attribute vec2 a_texCoord0;\n" +
	"uniform mat4 u_worldView;\n" +
	"uniform mat4 u_projTrans;\n" +
	"varying vec4 v_color;" +
	"varying vec2 v_texCoords;" +
	"void main()                  \n" +
	"{                            \n" +
	"   v_color = vec4(1, 1, 1, 1); \n" +
	"   v_texCoords = a_texCoord0; \n" +
	"   gl_Position =  u_projTrans * a_position;  \n" +
	"}                            \n";
String fragmentShader = "#ifdef GL_ES\n" +
	"precision mediump float;\n" +
	"#endif\n" +
	"varying vec4 v_color;\n" +
	"varying vec2 v_texCoords;\n" +
	"uniform sampler2D u_texture;\n" +
	"void main()                                  \n" +
	"{                                            \n" +
	"  gl_FragColor = v_color* texture2D(u_texture, v_texCoords);\n" +
	"}";
private int startX;
private int startY;
private BitmapFont font = new FreeTypeFontGenerator(Gdx.files.internal("assets/DejaVuSansMono.ttf")).generateFont(20);
private Texture bufTexture0 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture1 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture2 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private Texture bufTexture3 = new Texture(new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888));
private int cameraMoveStep = 5;

public GameScreen(final TendiwaGame game) {
	windowWidth = game.cfg.width;
	windowHeight = game.cfg.height;
	windowWidthCells = (int) Math.ceil(((float) windowWidth) / 32);
	windowHeightCells = (int) Math.ceil(((float) windowHeight) / 32);

	camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	camera.setToOrtho(true, windowWidth, windowHeight);

	this.game = game;

	atlas = new TextureAtlas("pack/package.atlas");
	pixmapTextureAtlas = new PixmapTextureAtlas(Gdx.files.internal("pack/package.png"), Gdx.files.internal("pack/package.atlas"));

	batch = new SpriteBatch();
	batch.setProjectionMatrix(camera.combined);

	cells = game.world.getCellContents();
	shader = new ShaderProgram(vertexShader, fragmentShader);
	font.setScale(1, -1);

	cursor = buildCursorTexture();

	maxStartX = TendiwaGame.WIDTH - windowWidthCells - cameraMoveStep;
	maxStartY = TendiwaGame.HEIGHT - windowHeightCells - cameraMoveStep;
	startX = 100;
	startY = 127;
}

@Override
public void render(float delta) {
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	// Draw whole floor tiles
	batch.begin();
	for (int x = 0; x < windowWidth / TILE_SIZE + 1; x++) {
		for (int y = 0; y < windowHeight / TILE_SIZE + 1; y++) {
			TextureRegion floor = getFloorTextureByCell(startX + x, startY + y);
			batch.draw(floor, x * TILE_SIZE, y * TILE_SIZE);
		}
	}

	int cursorX = Gdx.input.getX();
	int cursorY = Gdx.input.getY();
	font.draw(batch, Gdx.graphics.getFramesPerSecond() + "; " + startX + ":" + startY + ", mouse: " + cursorX + ":" + cursorY, 100, 100);
	batch.end();

	// Draw transitions
	for (int x = 0; x < windowWidth / TILE_SIZE + 1; x++) {
		for (int y = 0; y < windowHeight / TILE_SIZE + 1; y++) {
			getTransitionTextureByCell(startX + x, startY + y);
		}
	}

	// Process input
	if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
		if (startX > cameraMoveStep) {
			startX -= cameraMoveStep;
		}
	}
	if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
		if (startX < maxStartX) {
			startX += 5;
		}
	}
	if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
		if (startY > cameraMoveStep) {
			startY -= 5;
		}
	}
	if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
		if (startY < maxStartY) {
			startY += 5;
		}
	}
	int cursorScreenCoordX = (cursorX - cursorX % TILE_SIZE);
	int cursorScreenCoordY = (cursorY - cursorY % TILE_SIZE);
	batch.begin();
	batch.draw(cursor, cursorScreenCoordX, cursorScreenCoordY);
	batch.end();
}

private void getTransitionTextureByCell(int x, int y) {
	int self = cells[x][y].floor();
	int north = y - 1 > 0 ? cells[x][y + 1].floor() : self;
	int east = x + 1 < TendiwaGame.WIDTH ? cells[x + 1][y].floor() : self;
	int south = y + 1 < TendiwaGame.HEIGHT ? cells[x][y - 1].floor() : self;
	int west = x - 1 > 0 ? cells[x - 1][y].floor() : self;
//	if (floorTextures.containsKey(north)) {
//		HashMap<Integer, HashMap<Integer, HashMap<Integer, Texture>>> map2 = floorTextures.get(north);
//		if (map2.containsKey(east)) {
//			HashMap<Integer, HashMap<Integer, Texture>> map3 = map2.get(east);
//			if (map3.containsKey(south)) {
//				HashMap<Integer, Texture> map4 = map3.get(west);
//				if (map4.containsKey(west)) {
//					return map4.get(west);
//				}
//			}
//		}
//	}

	if (north != self || east != self || south != self || west != self) {
		drawCellWithTransitionsToFBO(x, y, north, east, south, west);
	}
}

private TextureRegion getFloorTextureByCell(int x, int y) {
	String name = FloorType.getById(cells[x][y].floor()).getName();
	return atlas.findRegion(
		name
	);
}

private void drawCellWithTransitionsToFBO(int x, int y, int north, int east, int south, int west) {
	// Get individual transition pixmap for each side
	Pixmap.Blending previousBlending = Pixmap.getBlending();
	Pixmap.setBlending(Pixmap.Blending.None);
	Pixmap transE = getTransition(Directions.E, east);
	Pixmap transS = getTransition(Directions.S, south);
	Pixmap transW = getTransition(Directions.W, west);
	Pixmap transN = getTransition(Directions.N, north);
	Pixmap.setBlending(previousBlending);

	// Transform Pixmaps to Textures
	bufTexture0.draw(transE, 0, 0);
	bufTexture1.draw(transS, 0, 0);
	bufTexture2.draw(transW, 0, 0);
	bufTexture3.draw(transN, 0, 0);

	// Drawing to framebuffer now
	batch.begin();
	batch.draw(bufTexture0, (x - startX) * TILE_SIZE, (y - startY) * TILE_SIZE);
	batch.draw(bufTexture1, (x - startX) * TILE_SIZE, (y - startY) * TILE_SIZE);
	batch.draw(bufTexture2, (x - startX) * TILE_SIZE, (y - startY) * TILE_SIZE);
	batch.draw(bufTexture3, (x - startX) * TILE_SIZE, (y - startY) * TILE_SIZE);
	batch.end();

//	HashMap<Integer, HashMap<Integer, HashMap<Integer, Texture>>> map3;
//	HashMap<Integer, HashMap<Integer, Texture>> map2;
//	HashMap<Integer, Texture> map1;
//	if (!floorTextures.containsKey(north)) {
//		map3 = new HashMap<>();
//		map2 = new HashMap<>();
//		map1 = new HashMap<>();
//		map1.put(west, sprite);
//		map2.put(south, map1);
//		map3.put(east, map2);
//		floorTextures.put(north, map3);
//	}
//	if (!floorTextures.containsKey(east)) {
//
//	}
//	return sprite;
}

private Pixmap getTransition(CardinalDirection dir, int floorId) {
	if (!floorTransitions.containsKey(dir)) {
		floorTransitions.put(dir, new HashMap<Integer, Pixmap>());
	}
	if (!floorTransitions.get(dir).containsKey(floorId)) {
		floorTransitions.get(dir).put(floorId, createTransition(dir, floorId));
	}
	return floorTransitions.get(dir).get(floorId);
}

private Pixmap createTransition(CardinalDirection dir, int floorId) {
	int diffusionDepth = 13;
	Pixmap pixmap = pixmapTextureAtlas.createPixmap(FloorType.getById(floorId).getName());
	CardinalDirection opposite = dir.opposite();
	EnhancedRectangle transitionRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(dir).createRectangle(diffusionDepth);
	EnhancedRectangle clearRec = DSL.rectangle(TILE_SIZE, TILE_SIZE).getSideAsSidePiece(opposite).createRectangle(TILE_SIZE - diffusionDepth);
	pixmap.setColor(0, 0, 0, 0);
	// Fill the most of the pixmap with transparent pixels.
	pixmap.fillRectangle(clearRec.x, clearRec.y, clearRec.width, clearRec.height);
	Segment sideSegment = transitionRec.getSideAsSegment(dir);
	EnhancedPoint point = new EnhancedPoint(sideSegment.x, sideSegment.y);
	pixmap.setColor(0, 0, 0, 0);
	CardinalDirection dynamicGrowingDir = dir.isVertical() ? Directions.E : Directions.S;
	int startI = sideSegment.getStaticCoord();
	int oppositeGrowing = opposite.getGrowing();
	int iterationsI = 0;
	for (
		int i = startI;
		i != startI + (diffusionDepth + 1) * opposite.getGrowing();
		i += oppositeGrowing
		) {
		for (
			int j = sideSegment.getStartCoord();
			j <= sideSegment.getEndCoord();
			j += 1
			) {
			if (Chance.roll((i - startI) / oppositeGrowing * 100 / diffusionDepth + 50)) {
				// Set transparent pixels to leave only some non-transparent ones.
				pixmap.drawPixel(point.x, point.y);
			}
			point.moveToSide(dynamicGrowingDir);
		}
		point.setLocation(sideSegment.x, sideSegment.y);
		point.moveToSide(opposite, iterationsI++);
	}
	return pixmap;
}

@Override
public void resize(int width, int height) {

}

@Override
public void show() {

}

@Override
public void hide() {

}

@Override
public void pause() {

}

@Override
public void resume() {

}

@Override
public void dispose() {

}


Texture buildCursorTexture() {
	Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
	pixmap.setColor(0, 1, 1, 0.3f);
	pixmap.fill();
	return new Texture(pixmap);
}
}
