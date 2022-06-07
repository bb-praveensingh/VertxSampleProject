package com.vertx.sample;

import com.bigbasket.core.common.di.VerticleScope;
import com.bigbasket.core.dal.kafka.bbproducer.Producer;
import com.bigbasket.core.dal.kafka.bbproducer.ProducerExtended;
import com.bigbasket.core.telemetry.LoggingUtils;
import dagger.Binds;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Named;

import static com.bigbasket.core.common.RequestContext.REQUEST_ENTRY_CONTEXT;
import static com.bigbasket.core.common.RequestContext.REQUEST_ENTRY_CONTEXT_ID;
import static com.bigbasket.core.common.RequestContext.REQUEST_PROJECT;

@VerticleScope
public class SampleService {

    @Inject
    @Named("cluster1")
    ProducerExtended producer1;

    @Inject
    @Named("cluster2")
    ProducerExtended producer2;

    @Inject
    protected Router router;

    @Inject
    public SampleService() {
    }

    public void setup() {
        this.router.postWithRegex("/sample/producer1")
                   .handler(produce1());
        this.router.postWithRegex("/sample/producer2")
                   .handler(produce2());
    }

    private Handler<RoutingContext> produce1() {
        JsonObject kafkaHeaders = new JsonObject();
        kafkaHeaders.put(LoggingUtils.RID, 1)
                    .put(REQUEST_ENTRY_CONTEXT, "b2c")
                    .put(REQUEST_ENTRY_CONTEXT_ID, 1)
                    .put(REQUEST_PROJECT, "Sample")
                    .put("X-Tracker", 1);
        return routingContext -> {
            final String bodyAsString = routingContext.getBodyAsString();
            System.out.println("producer1, body: " + bodyAsString);
            HttpServerResponse serverResponse = routingContext.response();
            producer1.rxPushWithHeaders("", "test", bodyAsString, kafkaHeaders)
                     .subscribe(x -> {
                         System.out.println("produce1, record added, " + x);
                         serverResponse.setStatusCode(200)
                                       .end();
                     }, x -> {
                         System.out.println(
                                 "producer1, failed" + x);
                         serverResponse.setStatusCode(500)
                                       .end();
                     });
        };
    }

    private Handler<RoutingContext> produce2() {
        JsonObject kafkaHeaders = new JsonObject();
        kafkaHeaders.put(LoggingUtils.RID, 2)
                    .put(REQUEST_ENTRY_CONTEXT, "b2c")
                    .put(REQUEST_ENTRY_CONTEXT_ID, 2)
                    .put(REQUEST_PROJECT, "Sample")
                    .put("X-Tracker", 2);
        return routingContext -> {
            final String bodyAsString = routingContext.getBodyAsString();
            System.out.println("producer2, body: " + bodyAsString);
            HttpServerResponse serverResponse = routingContext.response();
            producer2.rxPushWithHeaders("", "test_copy", bodyAsString, kafkaHeaders)
                     .subscribe(x -> {
                         System.out.println("produce2, record added, " + x);
                         serverResponse.setStatusCode(200)
                                       .end();
                     }, x -> {
                         System.out.println(
                                 "producer2, failed" + x);
                         serverResponse.setStatusCode(500)
                                       .end();
                     });
        };
    }
}
