package com.tendiwa.client.html;

import com.tendiwa.client.core.TendiwaLibgdxClient;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class TendiwaLibgdxClientHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new TendiwaLibgdxClient();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
