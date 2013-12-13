package org.tendiwa.client;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class SpellProjectileFireballActor extends ParticleEffectActor {
public SpellProjectileFireballActor(int x, int y) {
	super("assets/test.p", "/home/suseika/Projects/tendiwa/MainModule/data/images/chardoll");
	effect = createParticleEffect();
}

private ParticleEffect createParticleEffect() {
	return effect;
}
}
