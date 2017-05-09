package io.sugo.kafka;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Robot
{


    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("kafka.properties"));

        final String mytopic = props.get("mytopic").toString();
        final int rowPerSecond = Integer.valueOf(props.getProperty("rowPerSecond", "100"));
        final String startDate = props.getProperty("startDate", "2017-04-01T").toString();
        final String endDate = props.getProperty("endDate", "2017-05-01T").toString();
        final KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);

        final Random rand = new Random();
        DateTime startTime = new DateTime(startDate);
        DateTime endTime = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay();
        DateTime time = startTime;
        DateTime tmpTime = time;
        int cnt = 1;
        while (time.isBefore(endTime)){
            for (int i = 0; i < rowPerSecond; i++) {
                producer.send(new ProducerRecord<>(
                    mytopic,
                    UserAction.getAction(tmpTime)
                ));
                tmpTime = tmpTime.plusMillis(rand.nextInt(1000 / rowPerSecond));
            }
            time = time.plusSeconds(1);
            tmpTime = time;
            if(cnt++ % 10000 == 0){
                System.out.println(time);
            }
        }
    }





}
