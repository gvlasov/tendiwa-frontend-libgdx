package org.tendiwa.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.TendiwaClient;

public class DesktopStarter {
private final TendiwaLibgdxClient client;

DesktopStarter(TendiwaLibgdxClient client) {
	this.client = client;
}

}