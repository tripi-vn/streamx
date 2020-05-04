package vn.vntravel.consumer;

import vn.vntravel.Streamx;
import vn.vntravel.StreamxContext;

public interface ConsumerFactory {
    AbstractConsumer createConsumer(StreamxContext context);
}
