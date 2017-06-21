package io.sugo.nissan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static io.sugo.nissan.Constants.ADDR_CITY;
import static io.sugo.nissan.Constants.ADDR_PROVINCE;
import static io.sugo.nissan.Constants.CARD_TYPE;
import static io.sugo.nissan.Constants.CAR_SERIES_CODE;
import static io.sugo.nissan.Constants.CAR_TYPE_CODE;
import static io.sugo.nissan.Constants.CAR_YEAR;
import static io.sugo.nissan.Constants.CUST_SOURCE;
import static io.sugo.nissan.Constants.DLR_SHORT_NAME;
import static io.sugo.nissan.Constants.GUARANTEE_STATE;
import static io.sugo.nissan.Constants.LISENCE_TYPE;
import static io.sugo.nissan.Constants.MEMBER_LEVEL;
import static io.sugo.nissan.Constants.REPAIR_STATE;
import static io.sugo.nissan.Constants.REPAIR_TYPE;
import static io.sugo.nissan.Constants.boyNames;
import static io.sugo.nissan.Constants.familyName;
import static io.sugo.nissan.Constants.getTel;
import static io.sugo.nissan.Constants.girlNames;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class NissanDataCreator implements Closeable {
  private static final ObjectMapper jsonMapper = new ObjectMapper();
  private static final String mytopic = "nissan0607";

  private final KafkaProducer<Integer, String> producer;
  private final Random random = new Random();
  private final DateTime now = new DateTime();
  private MessageDigest md;

  public NissanDataCreator() {
    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.0.220:9092,192.168.0.221:9092,192.168.0.222:9092");
    props.put("client.id", "DemoProducer1");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<>(props);
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  private static final String base = "abcdefghijklmnopqrstuvwxyz0123456789";

  public String randomString(int length) { //length表示生成字符串的长度
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  public String randomString(String str, int length) { //length表示生成字符串的长度
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(str.length());
      sb.append(str.charAt(number));
    }
    return sb.toString();
  }

  public static void main(String[] args) throws IOException {
    NissanDataCreator creator = new NissanDataCreator();
    try {
      int sended = 0;
      long s1 = System.currentTimeMillis();
      for (int i = 0; i < 10000; i++) {
//      for (int i = 0; i < 10; i++) {
        List<String> msgs = creator.generateData(i);
        for (String msg : msgs) {
//          System.out.println(msg);
          creator.send(msg);
          sended++;
        }
      }
      long s2 = System.currentTimeMillis();
      System.out.println(String.format("sended:%d, spendTime:%d (s), time:%s", sended, (s2 - s1) / 1000, new DateTime()));
    } catch (Exception e) {

    } finally {
      creator.close();
    }
  }

  public void send(String msg) {
    producer.send(new ProducerRecord<>(mytopic, msg));
  }

  @Override public void close() throws IOException {
    producer.close();
  }

  private String getMd5Sum(String pos) {
    StringBuffer sb = new StringBuffer();
    md.reset();
    md.update(pos.getBytes());
    byte[] digest = md.digest();
    for (byte b : digest) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }

  //  SDP用户ID               SDP_USER_ID                STRING        随机数
  //  客户来源                CUST_SOURCE                STRING        开拓、来电、来店、亲朋介绍
  //  客户全称                CUST_FULL_NAME             STRING        姓名或公司名
  //  性别                    GENDER                     STRING        男、女
  //  电话                    PHONE                      STRING        手机号码
  //  联系地址-省份           ADDR_PROVINCE              STRING        广东省、四川省
  //  联系地址-城市           ADDR_CITY                  STRING        "广东省：广州市、深圳市、珠海市、中山市、东莞市、佛山市；四川省：成都市、绵阳市、德阳市"
  //  客户驾证类型            CUST_LISENCE_TYPE          STRING        C1、C2、B1
  //  厂家可用积分            PV_CAN_USE_POINT           INT            100-50000
  //  厂家累计积分            PV_TOTAL_POINT             INT            100-500000，应大于厂家可用积分。
  //  专营店可用积分          DLR_CAN_USE_POINT          INT            100-50000
  //  专营店累计积分          DLR_TOTAL_POINT            INT            100-500000，应大于当前专营店可用积分
  //  会员卡消费总额          USE_CARD_BALANCE           INT            100-100000
  //  现金消费总额            USE_CASH                   INT            100-100000
  //  厂家积分使用量          USE_PV_POINT               INT            100-500000，应小于厂家累计积分
  //  专营店积分使用量        USE_DLR_POINT              INT            100-500000，应小于专营店累计积分
  //  厂家积分抵用金额        PV_POINT_OFFSET_MONEY      INT            0-1000
  //  厂家积分兑换比例        PV_USE_POINT_RATE          float            （厂家累计积分-厂家可用积分）/厂家累计积分
  //  专营店积分抵用金额      DLR_POINT_MOMENY           INT            专营店积分抵用金额：金额数值，0-1000
  //  专营店积分兑换比例      DLR_USE_POINT_RATE         float            专营店积分兑换比例：（当前专营店累计积分-当前专营店可用积分）/当前专营店累计积分
  //////  累计消费金额            TOTAL_CONSUME_AMOUNT       INT            "应等于同一SDP_USER_ID下，时间早于MAINTAIN_DATE的USE_CARD_BALANCE、USE_CASH、USE_PV_POINT之和。若MAINTAIN_DATE为空，则比对REPAIR_TIME。"
  //  会员等级                MEMBER_LEVEL               STRING        钻石、铂金、黄金、白银、青铜
  //  会员卡类别              CARD_TYPE                  STRING        实物卡、电子卡
  //  会员卡余额              MEMBER_CARD_BALANCE        INT            100-10000
  //  保养日期                MAINTAIN_DATE              DATE        保养日期和维修时间，二者只同时存在一个有数据，另一个为空。
  //  平均保养频率（里程）    MAINTAIN_FREQ_MILEAGE      INT            5000-12000
  //  平均保养频率（日）      MAINTAIN_FREQ_DAY          INT            100-365
  //  日均里程                AVERAGE_MILE               INT            20-100
  ////  行驶里程              MILEAGE                    INT            5000-150000，同一车籍ID的行驶里程是个累计增加的数值，应按照保养时间/保修时间逐渐增加。
  //  车籍ID                  CAR_ID                     STRING        随机数
  //  车龄                    CAR_YEAR                   STRING        车龄：0、1、2、3、4、5年以上
  //  品牌编码                CAR_BRAND_CODE             STRING        日产
  //  车系编码                CAR_SERIES_CODE            STRING        三厢车：天籁、阳光、蓝鸟、轩逸，两厢车：骐达，SUV：奇骏、逍客、楼兰
  //  车型中类编码            CAR_TYPE_CODE              STRING        三厢车、两厢车、SUV、原装进口，注意与车系的对应。
  //  维修ID                  REPAIR_PAPER_ID            STRING        随机数
  //  维修类型                REPAIR_TYPE                STRING        维修类型：事故维修、故障维修、其他
  //  维修状态                REPAIR_STATE               STRING        维修状态：维修中，已完成
  //  维修时间                REPAIR_TIME                DATE        保养日期和维修时间，二者只同时存在一个有数据，另一个为空。
  //  保修状态                GUARANTEE_STATE            STRING        在保、过保
  //  保修到期时间            GUARANTEE_END_DATE         DATE        同一车籍ID的保修到期时间均相同。

  private List<String> generateData(int num) throws JsonProcessingException {
    List<String> msgs = new ArrayList<>(10);
    String cust_source = CUST_SOURCE[random.nextInt(CUST_SOURCE.length)];
    String user_id = getMd5Sum(num + "");
    String full_name;
    String gender;
    boolean flag = random.nextInt(10) > 3;
    if (flag) {
      full_name = randomString(familyName, 1) + randomString(boyNames, 2);
      gender = "男";
    } else {
      full_name = randomString(familyName, 1) + randomString(girlNames, 2);
      gender = "女";
    }
    String phone = getTel();
    int province = random.nextInt(ADDR_PROVINCE.length);
    int city = random.nextInt(ADDR_CITY[province].length);
    String lisence_type = LISENCE_TYPE[random.nextInt(LISENCE_TYPE.length)];

    String member_level = MEMBER_LEVEL[random.nextInt(MEMBER_LEVEL.length)];
    final int maintainTimes = random.nextInt(18) + 1;
    DateTime maintain_date = now.minusDays(random.nextInt(100)).minusDays(maintainTimes * 180);
    DateTime guarantee_end_date = maintain_date.plusYears(3);
    int randMaintainDays = 0;

    int MILEAGE = 0;
    String car_id = randomString(6);
    String car_year;
    if (maintainTimes < 9) {
      car_year = CAR_YEAR[maintainTimes / 2];
    } else {
      car_year = CAR_YEAR[CAR_YEAR.length - 1];
    }

    int carType = random.nextInt(CAR_TYPE_CODE.length);
    int seriesCode = random.nextInt(CAR_SERIES_CODE[carType].length);

    int MAINTAIN_FREQ_MILEAGE = 5000 + random.nextInt(12000 - 5000);
    int MAINTAIN_FREQ_DAY = 100 + random.nextInt(365 - 100);

    boolean maintain = true;
    DateTime repair_time = now.minusDays(maintainTimes * 180);
    String repair_paper_id = "";
    String repair_type = "";
    String repair_state = "";

    for (int idx = 0; idx < maintainTimes; idx++) {
      maintain = random.nextBoolean();
      if (maintain) {
        maintain_date = maintain_date.minusDays(randMaintainDays);
        randMaintainDays = random.nextInt(60);
        if (random.nextBoolean()) {
          randMaintainDays = -1 * randMaintainDays;
        }
        maintain_date = maintain_date.plusDays(randMaintainDays + 120);
        repair_paper_id = "";
        repair_type = "";
        repair_state = "";
      } else {
        repair_time = maintain_date.plusDays(120);
        repair_paper_id = randomString(8);
        repair_type = REPAIR_TYPE[random.nextInt(REPAIR_TYPE.length)];
        repair_state = REPAIR_STATE[random.nextInt(REPAIR_STATE.length)];
      }
      Map<String, Object> map = new HashMap<>();
      StringBuilder builder = new StringBuilder();
      map.put("d|create_time", now.getMillis());
      map.put("s|SDP_USER_ID", user_id);
      map.put("s|CUST_SOURCE", cust_source);
      map.put("s|CUST_FULL_NAME", full_name);
      map.put("s|GENDER", gender);
      map.put("s|PHONE", phone);
      map.put("s|ADDR_PROVINCE", ADDR_PROVINCE[province]);
      map.put("s|PHONE", phone);
      map.put("s|ADDR_CITY", ADDR_CITY[province][city]);
      map.put("s|CUST_LISENCE_TYPE", lisence_type);
      int PV_CAN_USE_POINT = 100 + random.nextInt(50000 - 100);
      map.put("i|PV_CAN_USE_POINT", PV_CAN_USE_POINT);

      int PV_TOTAL_POINT = PV_CAN_USE_POINT + random.nextInt(500000 - PV_CAN_USE_POINT);
      map.put("i|PV_TOTAL_POINT", PV_TOTAL_POINT);

      int DLR_CAN_USE_POINT = 100 + random.nextInt(50000 - 100);
      map.put("i|DLR_CAN_USE_POINT", DLR_CAN_USE_POINT);
      int DLR_TOTAL_POINT = DLR_CAN_USE_POINT + random.nextInt(500000 - DLR_CAN_USE_POINT);
      map.put("i|DLR_TOTAL_POINT", DLR_TOTAL_POINT);

      int USE_CARD_BALANCE = 100 + random.nextInt(100000 - 100);
      builder.append("USE_CARD_BALANCE=" + USE_CARD_BALANCE).append("&");
      map.put("i|USE_CARD_BALANCE", USE_CARD_BALANCE);
      int USE_CASH = 100 + random.nextInt(100000 - 100);
      map.put("i|USE_CASH", USE_CASH);
      int USE_PV_POINT = 100 + random.nextInt(PV_TOTAL_POINT - 100);
      map.put("i|USE_PV_POINT", USE_PV_POINT);
      int USE_DLR_POINT = 100 + random.nextInt(DLR_TOTAL_POINT - 100);
      map.put("i|USE_DLR_POINT", USE_DLR_POINT);
      map.put("i|PV_POINT_OFFSET_MONEY", random.nextInt(1000));
      float PV_USE_POINT_RATE = (PV_TOTAL_POINT - PV_CAN_USE_POINT) * 1.0f / PV_TOTAL_POINT;
      map.put("f|PV_USE_POINT_RATE", String.format("%.4f", PV_USE_POINT_RATE));
      int DLR_POINT_MOMENY = random.nextInt(1000);
      map.put("i|DLR_POINT_MOMENY", DLR_POINT_MOMENY);
      float DLR_USE_POINT_RATE = (DLR_TOTAL_POINT - DLR_CAN_USE_POINT) * 1.0f / DLR_TOTAL_POINT;
      map.put("f|DLR_USE_POINT_RATE", String.format("%.4f", DLR_USE_POINT_RATE));

      map.put("s|MEMBER_LEVEL", member_level);
      map.put("s|CARD_TYPE", CARD_TYPE[random.nextInt(CARD_TYPE.length)]);
      int MEMBER_CARD_BALANCE = 100 + random.nextInt(10000 - 100);
      map.put("i|DLR_POINT_MOMENY", MEMBER_CARD_BALANCE);
      if (!maintain) {
        map.put("d|MAINTAIN_DATE", maintain_date.getMillis());
        map.put("d|aaaaa", maintain_date.toString());
        map.put("s|isMaintain", "true");
      } else {
        map.put("d|MAINTAIN_DATE", repair_time.getMillis());
        map.put("d|bbbbb", repair_time.toString());
        map.put("s|isMaintain", "false");
        map.put("s|REPAIR_PAPER_ID", repair_paper_id);
        map.put("s|REPAIR_TYPE", repair_type);
        map.put("s|REPAIR_STATE", repair_state);
      }

      map.put("i|MAINTAIN_FREQ_MILEAGE", MAINTAIN_FREQ_MILEAGE);
      map.put("i|MAINTAIN_FREQ_DAY", MAINTAIN_FREQ_DAY);
      int AVERAGE_MILE = 20 + random.nextInt(100 - 20);
      map.put("i|AVERAGE_MILE", AVERAGE_MILE);
      MILEAGE = MILEAGE + 4000 + random.nextInt(2000);
      map.put("i|MILEAGE", MILEAGE);
      map.put("s|CAR_ID", String.format("CID-%s", car_id));
      map.put("s|CAR_YEAR", car_year);
      map.put("s|CAR_BRAND_CODE", "日产");
      map.put("s|CAR_TYPE_CODE", CAR_TYPE_CODE[carType]);
      map.put("s|CAR_SERIES_CODE", CAR_SERIES_CODE[carType][seriesCode]);

      map.put("s|GUARANTEE_STATE", GUARANTEE_STATE[random.nextInt(GUARANTEE_STATE.length)]);
      map.put("d|GUARANTEE_END_DATE", guarantee_end_date.getMillis());

      int dlrIndex = random.nextInt(DLR_SHORT_NAME.length);
      map.put("s|DLR_CODE", getMd5Sum(DLR_SHORT_NAME[dlrIndex]));
      map.put("s|DLR_SHORT_NAME", DLR_SHORT_NAME[dlrIndex]);

      msgs.add(jsonMapper.writeValueAsString(map));
    }
    return msgs;
  }


}
