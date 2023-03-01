package com.mrserg86.EventsOfSmartContract;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class JavaKafkaConsumerExample {

    //public static List<BigInteger> walletAddresses;

    public static List<String> consume() {
        String server = "localhost:9092";
        String topicName = "test.topic1";
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

        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        TopicPartition tp = new TopicPartition(topicName, 0);
        List<TopicPartition> tps = Arrays.asList(tp);
        consumer.assign(tps);
        consumer.seekToBeginning(tps);

        ConsumerRecords<Long, String> consumerRecords = consumer.poll(30000);
        if (!consumerRecords.isEmpty()) {
            System.out.println("SUCCESS");
            System.out.println(consumerRecords.iterator().next().value());
            walletAddresses.add(String.valueOf(consumerRecords.iterator().next().value()));
        }

        consumer.close();

        return walletAddresses;
    }

}
