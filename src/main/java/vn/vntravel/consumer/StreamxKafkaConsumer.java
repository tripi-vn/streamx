package vn.vntravel.consumer;

import vn.vntravel.StreamxContext;
import vn.vntravel.util.StoppableTask;

import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class StreamxKafkaConsumer extends AbstractConsumer {

    public StreamxKafkaConsumer(StreamxContext context, Properties kafkaProperties, String kafkaTopic) {
        super(context);

    }
}

class StreamxKafkaProducerWorker extends AbstractAsyncConsumer implements Runnable, StoppableTask {

    public StreamxKafkaProducerWorker(StreamxContext context) {
        super(context);
    }

    @Override
    public void run() {

    }

    @Override
    public void requestStop() throws Exception {

    }

    @Override
    public void awaitStop(Long timeout) throws TimeoutException {

    }
}


