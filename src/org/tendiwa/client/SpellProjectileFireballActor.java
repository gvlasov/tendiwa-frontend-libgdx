package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class SpellProjectileFireballActor extends ParticleEffectActor {
public SpellProjectileFireballActor(int x, int y) {
	super("assets/test.p", AtlasProjectiles.getInstance());
	effect = createParticleEffect();
	setPosition(x+0.5f, y+0.5f);
}

private ParticleEffect createParticleEffect() {
	return effect;
}
}
