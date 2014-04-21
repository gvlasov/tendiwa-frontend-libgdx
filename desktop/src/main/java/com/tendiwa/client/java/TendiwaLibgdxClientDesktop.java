package com.tendiwa.client.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.tendiwa.client.core.TendiwaLibgdxClient;

public class TendiwaLibgdxClientDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		new LwjglApplication(new TendiwaLibgdxClient(), config);
	}
}
