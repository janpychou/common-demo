package io.sugo.kafka.offset;

import com.google.common.collect.Lists;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.util.Properties;

public class KafkaOffsetTest {
  public static void main(String[] args) {
    KafkaConsumer consumer = getKafkaConsumer();
    boolean useEarliestOffset = true;

    TopicPartition topicPartition = new TopicPartition("bigdata", 0);
    if (!consumer.assignment().contains(topicPartition)) {
      consumer.assign(Lists.newArrayList(topicPartition));
    }

    if (useEarliestOffset) {
      consumer.seekToBeginning(topicPartition);
    } else {
      consumer.seekToEnd(topicPartition);
    }
    long ret = consumer.position(topicPartition);
    System.out.println(6);
    System.out.println(ret);
  }

  private static KafkaConsumer<byte[], byte[]> getKafkaConsumer() {
    final Properties props = new Properties();
//    props.setProperty("bootstrap.servers", "192.168.0.220:9092,192.168.0.221:9092,192.168.0.222:9092");
    props.setProperty("bootstrap.servers", "192.168.0.223:9092,192.168.0.224:9092,192.168.0.225:9092,192.168.0.226:9092,192.168.0.227:9092");
    props.setProperty("retry.backoff.ms", "3000");
    //    props.setProperty("enable.auto.commit", "false");
    //    props.setProperty("metadata.max.age.ms", "10000");
    props.setProperty("group.id", "kafka-supervisor-111234fsafd11");
//    props.setProperty("auto.offset.reset", "");

    try {
      return new KafkaConsumer<>(props, new ByteArrayDeserializer(), new ByteArrayDeserializer());
    } finally {
    }
  }
}
