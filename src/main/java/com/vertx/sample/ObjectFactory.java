package com.vertx.sample;

import com.bigbasket.core.common.di.KafkaProducerModule;
import com.bigbasket.core.common.di.VerticleScope;
import com.bigbasket.core.tenant.TenantSettings;
import dagger.BindsInstance;
import dagger.Component;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

import java.util.Optional;

@VerticleScope
@Component(modules = SampleKafkaProducerModule.class)
public interface ObjectFactory {

    @VerticleScope
    SampleService sampleService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder injectVertx(Vertx vertx);

        @BindsInstance
        Builder injectConfig(JsonObject config);

        @BindsInstance
        Builder injectRouter(Router router);

        @BindsInstance
        Builder injectVertx(io.vertx.core.Vertx vertx);

        ObjectFactory build();
    }
}
