package vn.vntravel.consumer;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import vn.vntravel.StreamxContext;
import vn.vntravel.util.StoppableTask;

public abstract class AbstractConsumer {
    protected final StreamxContext context;
//    protected final MaxwellOutputConfig outputConfig;
    protected Counter succeededMessageCount;
    protected Meter succeededMessageMeter;
    protected Counter failedMessageCount;
    protected Meter failedMessageMeter;
    protected Timer messagePublishTimer;
    protected Timer messageLatencyTimer;
    protected Counter messageLatencySloViolationCount;

    public AbstractConsumer(StreamxContext context) {
        this.context = context;
//        this.outputConfig = context.getConfig().outputConfig;
//
//        Metrics metrics = context.getMetrics();
//        MetricRegistry metricRegistry = metrics.getRegistry();
//
//        this.succeededMessageCount = metricRegistry.counter(metrics.metricName("messages", "succeeded"));
//        this.succeededMessageMeter = metricRegistry.meter(metrics.metricName("messages", "succeeded", "meter"));
//        this.failedMessageCount = metricRegistry.counter(metrics.metricName("messages", "failed"));
//        this.failedMessageMeter = metricRegistry.meter(metrics.metricName("messages", "failed", "meter"));
//        this.messagePublishTimer = metricRegistry.timer(metrics.metricName("message", "publish", "time"));
//        this.messageLatencyTimer = metricRegistry.timer(metrics.metricName("message", "publish", "age"));
//        this.messageLatencySloViolationCount = metricRegistry.counter(metrics.metricName("message", "publish", "age", "slo_violation"));
    }

    public StoppableTask getStoppableTask() {
        return null;
    }
}
