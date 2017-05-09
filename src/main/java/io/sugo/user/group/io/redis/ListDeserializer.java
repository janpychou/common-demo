package io.sugo.user.group.io.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by janpychou on 上午11:19.
 * Mail: janpychou@qq.com
 */
public class ListDeserializer {

  public static List<String> deserialize(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return Collections.emptyList();
    }

    String str = new String(bytes);
    StringTokenizer tokenizer = new StringTokenizer(str);
    final int total = Integer.valueOf(tokenizer.nextToken());
    List<String> data = new ArrayList<>(total);
    while (tokenizer.hasMoreElements()) {
      data.add(tokenizer.nextToken());
    }

    return data;
  }
}
