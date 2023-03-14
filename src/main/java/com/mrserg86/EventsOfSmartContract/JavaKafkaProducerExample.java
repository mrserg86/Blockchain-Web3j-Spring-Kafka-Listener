package com.mrserg86.EventsOfSmartContract;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.mrserg86.EventsOfSmartContract.ListenerOfTransactions.txBingo;

public class JavaKafkaProducerExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String server = "localhost:9092";
        String topicName = "topicIn";

        final Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        final Producer<Long, String> producer =
                new KafkaProducer<>(props);

//        RecordMetadata recordMetadata = (RecordMetadata) producer.send(new ProducerRecord(topicName, txBingo.iterator().next().getValue())).get();
//        if (recordMetadata.hasOffset())
//            System.out.println("Message sent successfully");



        RecordMetadata recordMetadata = (RecordMetadata) producer.send(new ProducerRecord(topicName, "0xeDc5c0029309cA5576D452456228ca0B1fE8b9a3")).get();
        if (recordMetadata.hasOffset())
            System.out.println("Message sent successfully");

        producer.close();
    }

}
