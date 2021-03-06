package org.kmspan.core;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SpanMessageUtils {

    /**
     * convert a wire message from consumer to a user message
     *
     * @param wireMessage
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ConsumerRecord<K, V> toUserMessage(ConsumerRecord<SpanData<K>, V> wireMessage) {
        if (!wireMessage.key().isSpanMessage()) {
            return new ConsumerRecord<>(wireMessage.topic(), wireMessage.partition(), wireMessage.offset(),
                    wireMessage.key().getData(), wireMessage.value());
        }
        return null;
    }

    /**
     * convert a wire message from consumer to a span message (NOT event)
     *
     * @param wireMessage
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ConsumerSpanEvent toSpanMessage(ConsumerRecord<SpanData<K>, V> wireMessage) {
        if (wireMessage.key().isSpanMessage()) {
            return ConsumerSpanEvent.createSpanMessage(wireMessage.timestampType(),
                    wireMessage.timestamp(), wireMessage.key().getSpanId(),
                    wireMessage.key().getSpanEventType(), wireMessage.topic());
        }
        return null;
    }

    /**
     * convert a user message to a wire message for producer
     *
     * @param record
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ProducerRecord<SpanData<K>, V> toUserMessage(ProducerRecord<K, V> record) {
        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(),
                new SpanData<>(record.key()), record.value()
        );
    }

    public static <K, V> ProducerRecord<SpanData<K>, V> toSpanMessage(
            ProducerRecord<K, V> record, String spanId, String spanMessageType) {
        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(),
                new SpanData<>(spanId, spanMessageType, record.key()), record.value()
        );
    }
}
