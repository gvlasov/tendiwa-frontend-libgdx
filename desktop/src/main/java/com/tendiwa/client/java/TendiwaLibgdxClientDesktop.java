package com.tendiwa.client.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.tendiwa.client.TendiwaLibgdxClient;


public class TendiwaLibgdxClientDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
		new LwjglApplication(new TendiwaLibgdxClient(), config);
	}
}
