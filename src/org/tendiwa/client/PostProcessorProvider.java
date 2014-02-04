package org.tendiwa.client;

import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.utils.ShaderLoader;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class PostProcessorProvider implements Provider<PostProcessor> {
@Inject
PostProcessorProvider() {
	ShaderLoader.BasePath = "shaders/postprocessing/";
	ShaderLoader.Pedantic = false;
//	CrtMonitor effect = new CrtMonitor(1024, 768, true, true, CrtScreen.RgbMode.ChromaticAberrations, 0);

//	Bloom bloom = new Bloom(1024, 768);
//	Curvature curve = new Curvature();
//	curve.setDistortion(0.5f);
//	bloom.setBloomIntesity(1.0f);
//	postProcessor.addEffect(curve);
//	postProcessor.addEffect(bloom);
//	CrtMonitor effect1 = new CrtMonitor(1024, 768, false, true, CrtScreen.RgbMode.ChromaticAberrations, 8);
//	effect1.setTime(1);
//	postProcessor.addEffect(effect1);
}
@Override
public PostProcessor get() {
	return new PostProcessor(false, false, true);
}
}
