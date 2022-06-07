package com.vertx.sample;

import com.bigbasket.core.common.di.VerticleScope;
import com.bigbasket.core.dal.kafka.bbproducer.Producer;
import com.bigbasket.core.dal.kafka.bbproducer.ProducerExtended;
import com.bigbasket.core.tenant.TenantSettings;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.ProducerConfig;

import javax.inject.Named;
import java.util.Optional;

@Module
public class SampleKafkaProducerModule {

    //Returning empty as multi-tenant not configured here.
    @Provides
    @VerticleScope
    static Optional<TenantSettings> getTenantSettings(JsonObject config) {
        return Optional.empty();
    }


    @Provides
    @VerticleScope
    @Named("cluster1")
    static ProducerExtended producerExtended1(Vertx vertx, JsonObject config, Optional<TenantSettings> tenantSettings) {
        System.out.println("producerExtended1, config: " + config);
        final Producer producer = Producer.createProxy(vertx, config);
        System.out.println("Producer1: " + producer);
        return new ProducerExtended(producer, tenantSettings);
    }

    @Provides
    @VerticleScope
    @Named("cluster2")
    static ProducerExtended producerExtended2(Vertx vertx, JsonObject config, Optional<TenantSettings> tenantSettings) {
        final JsonObject entries = new JsonObject(config.getMap());
        entries.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9098");
        System.out.println("producerExtended2, config: " + config);
        final Producer producer = Producer.createProxy(vertx, entries);
        System.out.println("Producer2: " + producer);
        return new ProducerExtended(producer, tenantSettings);
    }
}
