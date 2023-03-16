package com.mrserg86.EventsOfSmartContract;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class JavaKafkaProducerExample {

    public static void produce(String addressFrom, String addressTo, String howMuch) throws ExecutionException, InterruptedException {
        String server = "localhost:9092";
        String topicName = "topicOut";

        final Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        final Producer<Long, String> producer =
                new KafkaProducer<>(props);

        RecordMetadata recordMetadata = (RecordMetadata) producer.send
                (new ProducerRecord(topicName, "From address " + addressFrom + " was transaction to " + addressTo + "; Quantity: " + howMuch)).get();
        if (recordMetadata.hasOffset())
            System.out.println("Message sent successfully");

        producer.close();
    }

}
