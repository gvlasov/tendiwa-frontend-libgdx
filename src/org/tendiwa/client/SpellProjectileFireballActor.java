package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class SpellProjectileFireballActor extends ParticleEffectActor {
public SpellProjectileFireballActor(int x, int y) {
	super("assets/test.p", AtlasProjectiles.getInstance());
	effect = createParticleEffect();
	setPosition(x, y);
}

private ParticleEffect createParticleEffect() {
	return effect;
}
}
