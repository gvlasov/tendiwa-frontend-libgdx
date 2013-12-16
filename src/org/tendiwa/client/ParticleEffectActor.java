package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
ParticleEffect effect;

public ParticleEffectActor(String particleEffect, TextureAtlas atlas) {
	this.effect = new ParticleEffect();
	effect.load(Gdx.files.internal(particleEffect), atlas);
}

@Override
public void draw(Batch batch, float parentAlpha) {
	effect.draw(batch);
}

public void act(float delta) {
	super.act(delta);
	effect.setPosition(getX() * GameScreen.TILE_SIZE, getY() * GameScreen.TILE_SIZE);
	effect.update(delta);
	effect.start();
}

public ParticleEffect getEffect() {
	return effect;
}
}