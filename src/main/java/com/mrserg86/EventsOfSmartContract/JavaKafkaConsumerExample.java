package com.mrserg86.EventsOfSmartContract;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

public class JavaKafkaConsumerExample {

    public static List<String> consume() {
        String server = "localhost:9092";
        String topicName = "test";
        String groupName = "test.group";
        List<String> walletAddresses = new ArrayList<>();

        final Properties props = new Properties();

        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                groupName);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                server);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        // Create the consumer using props
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic
        consumer.subscribe(Collections.singletonList(topicName));

        final int giveUp = 1;   int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            walletAddresses.add(String.valueOf(consumerRecords.iterator().next().value()));
            consumerRecords.forEach(record -> {
                System.out.println(record.value());
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");





//        TopicPartition tp = new TopicPartition(topicName, 0);
//        List<TopicPartition> tps = Arrays.asList(tp);
//        consumer.assign(tps);
//        consumer.seekToBeginning(tps);
//
//        ConsumerRecords<Long, String> consumerRecords = consumer.poll(30000);
//
//            if (!consumerRecords.isEmpty()) {
//                System.out.println("Адрес в консумер получен");
//                System.out.println("Список адресов для прослушивания: " + consumerRecords.iterator().next().value());
//                walletAddresses.add(String.valueOf(consumerRecords.iterator().next().value()));
//            }
//
//        consumer.close();
//
        return walletAddresses;
    }

}
