package com.vertx.sample;

import com.bigbasket.core.common.RequestContext;
import com.bigbasket.core.controller.BBCommonVerticle;
import io.vertx.core.Promise;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class SampleVerticle extends BBCommonVerticle {
    public static final String HEALTH_BASIC_URL = "/slotss/internal/v1/health";
    public static final String DEBUG_URL = "/slot/internal/v1/debug";
    private ObjectFactory factory;

    public SampleVerticle() {
        super(HEALTH_BASIC_URL, DEBUG_URL);
    }

    @Override
    public void start(Promise<Void> future) {
        router = Router.router(vertx);
        factory = DaggerObjectFactory.builder()
                                     .injectConfig(config())
                                     .injectVertx(vertx)
                                     .injectVertx(vertx.getDelegate())
                                     .injectRouter(router)
                                     .build();
        super.start(future);
        final SampleService sampleService = factory.sampleService();
        sampleService.setup();
        System.out.println("Sample Verticle started.");
    }
}
