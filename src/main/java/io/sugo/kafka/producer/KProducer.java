package io.sugo.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by root on 16-12-5.
 */
public class KProducer implements Runnable {

  private static String[] areas = new String[]{
      "延庆县", "顺义县", "通县", "和平区", "河东区", "河西区", "南开区", "宜宾市", "红桥区", "塘沽区", "汉沽区", "大港区", "东丽区", "西青区", "津南区", "北辰区", "宁河县", "武清县", "静海县", "宝坻县", "蓟县", "邯郸市大名县", "邯郸市涉县", "邯郸市磁县", "邯郸市肥乡县", "邯郸市永年县", "邯郸市邱县", "邯郸市鸡泽县", "邯郸市广平县", "邯郸市馆陶县", "邯郸市魏县", "邢台市隆尧县", "邢台市任县", "邢台市南和县", "邢台市宁晋县", "沧洲市郊区", "廊坊市市辖区", "廊坊市安次区", "沧州市泊头市", "沧州市任丘市", "沧洲市郊区", "定州市", "涿州市", "安国市", "高碑店市", "易县", "徐水县", "涞源县", "定兴县", "顺平县", "唐县", "望都县", "涞水县", "高阳县", "安新县", "雄县", "容城县", "曲阳县", "阜平县", "博野县", "蠡县", "太原市市辖区", "双城市", "尚志市", "五常市", "巴彦县", "木兰县", "通河县", "延寿县", "兰州市市辖区", "兰州市城关区", "吴忠市青铜峡市", "吴忠市灵武市", "固原地区固原县", "喀什地区泽普县", "喀什地区伽师县", "阿勒泰地区富蕴县", "贵阳市云岩区", "绥阳县", "正安县", "道真仡佬苗族自治县", "务川仡佬苗族自治县", "凤冈县", "湄潭县", "余庆县", "仁怀县", "习水县", "昆明市市辖区", "昆明市五华区", "铜川市郊区", "兴文县", "屏山县", "石柱土家族自治县", "秀山土家族苗族自治县", "黔江土家族苗族自治县", "彭水苗族土家族自治县", "华蓥市", "岳池县", "广安县", "武胜县"
  };

  private static Random rand = new Random();

  private Properties props;
  private CountDownLatch latch;
  private AtomicInteger produceCnt;
  private int sPos;
  private int ePos;
  private MessageDigest md;
  private Map<Integer, String> idMap;
  private AtomicLong produceTime;
  private AtomicInteger produceNum;
  private KafkaProducer<Integer, String> producer;
  private final String myTopic;
  private CountDownLatch syncLatch;
  private BlockingQueue<Future<RecordMetadata>> futureQueue;
  private final int numberDimNum;
  private final int strDimNum;
  private final DateTime dt = new DateTime();

  public KProducer(Properties props,
      BlockingQueue<Future<RecordMetadata>> futureQueue, CountDownLatch latch, CountDownLatch syncLatch, AtomicInteger produceCnt,
      int sPos, int ePos,
      AtomicLong produceTime, AtomicInteger produceNum, int dimNum) {
    this.props = props;
    this.futureQueue = futureQueue;
    this.latch = latch;
    this.syncLatch = syncLatch;
    this.produceCnt = produceCnt;
    this.sPos = sPos;
    this.ePos = ePos + 1;
    this.strDimNum = (dimNum - 3) / 2;
    this.numberDimNum = dimNum - 3 - strDimNum;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    idMap = new HashMap<>(ePos - sPos);
    this.produceTime = produceTime;
    this.produceNum = produceNum;
    props.put("acks", "0");
    producer = new KafkaProducer<>(props);

    myTopic = props.get("myTopic").toString();
    System.out.println(sPos + " " + ePos);
  }

  private KProducer(int dimNum) {
    myTopic = "";
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    idMap = new HashMap<>(ePos - sPos);
    this.strDimNum = (dimNum - 3) / 2;
    this.numberDimNum = dimNum - 3 - strDimNum;
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    try {
      String action;
      for (int pos = sPos; pos < ePos; pos++) {
        action = getAction(rand.nextInt(10000));
        producer.send(new ProducerRecord<>(myTopic, action));
        //          Future<RecordMetadata> ret = producer.send(new ProducerRecord<>(myTopic, action));
        //          futureQueue.put(ret);
        produceNum.incrementAndGet();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      long end = System.currentTimeMillis();
      long spend = end - start;
      produceCnt.addAndGet(produceNum.get());
      System.out.println(Thread.currentThread().getName() + " produced:" + produceNum.get() + " spend time:" + spend);
      latch.countDown();
      syncLatch.countDown();
    }
  }

  /**
   * 100w deviceId, 3 per minute, one year
   * id, ts, electricity(ele, 1~100), electricCurrent(eleCur, float), voltage(vol,float), area(100)
   *
   * @param pos
   */
  private String getAction(int pos) {
    StringBuffer sb = new StringBuffer();
    String val;
    sb.append("id=" + String.format("ID-%07d", pos)).append("&");
    sb.append("ts=" + new DateTime().getMillis()).append("&");

    for (int i = 0; i < strDimNum; i++) {
      //      StringBuffer tmp = new StringBuffer();
      //      int len = rand.nextInt(20) + 5;
      //      for (int j = 0; j < len; j++) {
      //        if (j % 2 == 0) {
      //          tmp.append((char) (rand.nextInt(26) + 65));
      //        } else {
      //          tmp.append((char) (rand.nextInt(26) + 97));
      //        }
      //      }
      val = getDeviceId(rand.nextInt(15 + i));
      sb.append(String.format("string_%d=%s&", i, val));
    }
    for (int i = 0; i < numberDimNum; i++) {
      sb.append(String.format("number_%d=%s&", i, rand.nextInt(10000)));
    }
    sb.append("area=" + areas[rand.nextInt(areas.length)]);
    return sb.toString();
  }

  private String getDeviceId(int pos) {
    String id = idMap.get(pos);
    if (id != null) {
      return id;
    }
    StringBuffer sb = new StringBuffer();
    md.reset();
    md.update(String.valueOf(pos).getBytes());
    byte[] digest = md.digest();
    for (byte b : digest) {
      sb.append(String.format("%02x", b & 0xff));
    }
    id = sb.toString();
    idMap.put(pos, id);
    return id;
  }

  public static void main(String[] args) {
    KProducer p = new KProducer(100);
    System.out.println(p.getDeviceId(100));
    DateTime dt = new DateTime();
    long s1 = System.currentTimeMillis();
    for (int i = 0; i < 100; i++) {
      p.getAction(i);
    }
    long s2 = System.currentTimeMillis();
    System.out.println(s2 - s1);
    s1 = System.currentTimeMillis();
    String action = p.getAction(10);
    System.out.println(action);
    System.out.println(action.length());
    for (int num = 0; num < 1; num++) {
      for (int i = 0; i < 10; i++) {
        action = p.getAction(i);
        System.out.println(action);
        System.out.println(action.length());
      }
    }
    s2 = System.currentTimeMillis();
    System.out.println(s2 - s1);
    System.out.println(p.getAction(100));
    System.out.println(areas.length);
    System.out.println(String.format("%s-%07d", "ID", 100));
    System.out.println((int) 'a');
  }
}
