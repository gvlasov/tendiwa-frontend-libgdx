package org.tendiwa.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.tendiwa.events.EventMove;
import tendiwa.core.PlayerCharacter;
import tendiwa.core.TendiwaClient;
import tendiwa.core.World;
import tendiwa.modules.MainModule;
import tendiwa.resources.CharacterTypes;

public class TendiwaGame extends Game implements TendiwaClient {

public static int WIDTH = 800;
public static int HEIGHT = 600;
final LwjglApplicationConfiguration cfg;
final World world;
final PlayerCharacter player;
private GameScreen gameScreen;

public TendiwaGame(LwjglApplicationConfiguration cfg) {
	this.cfg = cfg;
	world = new MainModule().createWorld();
	player = PlayerCharacter.resideToWorld(world, 150, 120, "Suseika", CharacterTypes.human);
}

@Override
public void create() {
	gameScreen = new GameScreen(this);
	setScreen(gameScreen);
}

@Override
public void render() {
	super.render();
}

@Override
public void event(EventMove e) {
	Actor characterActor = gameScreen.getCharacterActor(e.getCharacter());



}
}
