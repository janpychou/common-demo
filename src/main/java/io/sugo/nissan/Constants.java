package io.sugo.nissan;


public class Constants {
  public static final String[] CUST_SOURCE = new String[]{"开拓", "来电", "来店", "亲朋介绍"};
  public static final String familyName = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘" +
      "葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹" +
      "仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘姜詹束龙叶幸司韶郜黎蓟薄印宿白";
  public static final String boyNames = "绍功松厚庆磊民友裕河哲之轮翰朗伯宏言若鸣朋斌利清飞彬富顺信子杰涛昌林有坚和彪博诚先敬震振晨" +
      "辰士建家致炎德行时泰志武中榕奇鹏楠泽风博诚伟刚勇毅俊峰强军平东文兴良海山仁波宁贵福生龙江超浩亮政谦亨固成康星壮会思豪心邦承乐盛" +
      "雄琛先敬震振壮茂磊航辉力明元全国胜学祥才发光天达钧冠策腾弘永健世广义安晨轩清睿宝涛华国亮新凯志明伟嘉东洪建文子云杰兴友才振辰航" +
      "达鹏宇衡佳强宁丰波森学民永翔鸿海飞义生凡连良乐勇辉龙川宏谦锋双霆玉智增名进德聚军兵忠廷先江昌政君泽超信腾恒礼元磊阳月士洋欣升恩" +
      "迅科富函业胜震福瀚瑞朔津韵荣为诚斌广庆成峰可健英功冬锦立正禾平旭同全豪源安顺帆向雄材利希风林奇易来咏岩启坤昊朋和纪艺昭映威奎帅" +
      "星春营章高伦庭蔚益城牧钊刚洲家晗迎罡浩景珂策皓栋起棠登越盛语钧亿基理采备纶献维瑜齐凤毅谊贤逸卫万臻儒钢洁霖隆远聪耀誉继珑哲岚舜" +
      "钦琛金彰亭泓蒙祥意鑫朗晟晓晔融谋宪励璟骏颜焘垒尚镇济雨蕾韬选议曦奕彦虹宣蓝冠谱泰泊跃韦怡骁俊沣骅歌畅与圣铭溓滔溪巩影锐展笑祖时" +
      "略敖堂绍崇悦邦望尧珺然涵博淼琪群驰照传诗靖会力大山之中方仁世梓竹至充亦丞州言佚序宜";
  public static final String girlNames = "筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲" +
      "芬芳燕彩春菊勤珍贞莉兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩筠柔竹霭凝晓欢霄枫芸菲" +
      "寒伊亚宜可姬舒影荔枝思丽秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊勤珍贞莉兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞" +
      "凡佳嘉琼桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭" +
      "冰爽琬茗羽希宁欣飘育滢馥";

  public static final String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");

  public static int getNum(int start, int end) {
    return (int) (Math.random() * (end - start + 1) + start);
  }

  public static String getTel() {
    int index = getNum(0, telFirst.length - 1);
    String first = telFirst[index];
    String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
    String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
    return first + second + thrid;
  }

  public static final String[] ADDR_PROVINCE = "广东省,四川省".split(",");
  public static final String[][] ADDR_CITY = new String[][]{
      new String[]{"广州市", "深圳市", "珠海市", "中山市", "东莞市", "佛山市"},
      new String[]{"成都市", "绵阳市", "德阳市"}
  };
  public static final String[] LISENCE_TYPE = "C1、C2、B1".split("、");
  public static final String[] MEMBER_LEVEL = "钻石、铂金、黄金、白银、青铜".split("、");
  public static final String[] CARD_TYPE = "实物卡、电子卡".split("、");
  public static final String[] CAR_YEAR = "0、1、2、3、4、5年以上".split("、");

  public static final String[] CAR_TYPE_CODE = "三厢车、两厢车、SUV、原装进口".split("、");
  public static final String[][] CAR_SERIES_CODE = new String[][]{
      new String[]{"天籁", "阳光", "蓝鸟", "轩逸"},
      new String[]{"骐达"},
      new String[]{"奇骏", "逍客", "楼兰"},
      new String[]{"GT-R", "途乐"}
  };
  public static final String[] REPAIR_TYPE = "事故维修、故障维修、其他".split("、");
  public static final String[] REPAIR_STATE = "维修中、已完成".split("、");
  public static final String[] GUARANTEE_STATE = "在保、过保".split("、");
  public static final String[] DLR_SHORT_NAME = "广州绿日、广州东风南方广辰、深圳东风南方华新、深圳裕朋、都江堰欣荣铭泰、成都启阳锦江、绵阳东风南方绵兴、江油东风南方绵旺".split("、");
}
