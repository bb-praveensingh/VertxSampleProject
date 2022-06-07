package com.vertx.sample;

import com.bigbasket.core.dal.kafka.bbproducer.ProducerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends com.bigbasket.core.controller.MainVerticle{
    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    protected void rxDeployVertical(JsonObject config) {
        DeploymentOptions options = new DeploymentOptions().setConfig(config).setInstances(1).setWorkerPoolSize(1);
        vertx.deployVerticle(SampleVerticle.class, options);
        final JsonObject jsonObject1 = new JsonObject().put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                                            "localhost:9096").put("PRODUCER_NAME", "cluster1");
        vertx.deployVerticle(ProducerVerticle.class,
                             new DeploymentOptions().setConfig(
                                     jsonObject1)
                , handler -> {
            if (handler.succeeded()) {
                LOGGER.info("Producer1 deployment succeeded");
            }
            else {
                LOGGER.error("Producer1 deployment failed", handler.cause());
                System.exit(1);
            }
        });

        //bootstrap.servers and PRODUCER_NAME are mandatory to be provided.
        final JsonObject jsonObject2 =
                new JsonObject().put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9098").put("PRODUCER_NAME",
                                                                                                    "cluster2");
        vertx.deployVerticle(ProducerVerticle.class,
                             new DeploymentOptions().setConfig(
                                     jsonObject2)
                , handler -> {
                    if (handler.succeeded()) {
                        LOGGER.info("Producer2 deployment succeeded");
                    }
                    else {
                        LOGGER.error("Producer2 deployment failed", handler.cause());
                        System.exit(1);
                    }
                });
    }
}
