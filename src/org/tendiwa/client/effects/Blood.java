package org.tendiwa.client.effects;

import org.tendiwa.client.AtlasProjectiles;
import org.tendiwa.client.ParticleEffectActor;

public class Blood extends ParticleEffectActor {
public Blood(int x, int y) {
	super("assets/blood.p", AtlasProjectiles.getInstance());
	setPosition(x+0.5f, y+0.5f);
}
}
