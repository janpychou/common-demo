package io.sugo.kafka.user;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.Random;

public class UserEventRobot
{
    private static String[] provinces= {"北京市","天津市","河北省","山西省","内蒙古"
        ,"辽宁省","吉林省","黑龙江","上海市"
        ,"江苏省","浙江省","安微省","福建省","江西省","山东省","河南省","湖北省","湖南省","广东省","广西省","海南省"
        ,"重庆市","四川省","贵州省","云南省","西藏","陕西省","甘肃省"
        ,"青海省","宁夏","新疆","台湾"
        ,"香港","澳门"};
    private static String[] actions = {"唤醒","点击","浏览","后台","启动","对焦","退出"};

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("kafka.properties"));

        final String mytopic = "events";
        final KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);

        DateTime dateTime = new DateTime();
        System.out.println(dateTime);

        final Random rand = new Random();
        int randSeconds = 24 * 60 * 60 * 10;
        for(int i = 0; i < 5000000; i++) {
            StringBuffer msg = new StringBuffer();

            MessageDigest md = MessageDigest.getInstance("MD5");
            int num = rand.nextInt(100000);
            md.update(String.valueOf(num).getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            String userId = sb.toString();
            msg.append("rowId="+ i).append("&");
            msg.append("num="+ num).append("&");
            msg.append("UserID="+ userId).append("&");
            int age = rand.nextInt(20) + 10;
            msg.append("age="+ age).append("&");
            msg.append("score="+ rand.nextInt(100)).append("&");
            msg.append("province="+ provinces[rand.nextInt(provinces.length)]).append("&");
            msg.append("action="+ actions[rand.nextInt(actions.length)]).append("&");
            msg.append("duration="+ rand.nextInt(1000)).append("&");
            msg.append("birthday="+ dateTime.minusYears(age).plusDays(rand.nextInt(200)).plusSeconds(rand.nextInt(86400)).plusMillis(rand.nextInt(3000)).getMillis()).append("&");
            msg.append("create_time="+ dateTime.minusSeconds(rand.nextInt(randSeconds)).plusMillis(rand.nextInt(3000)).getMillis());

//            System.out.println(msg.toString());
            producer.send(new ProducerRecord<>(mytopic, msg.toString()));
        }

        producer.close();
        System.out.println("send ok, " + new DateTime());
    }





}
