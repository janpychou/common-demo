/*
 * Copyright (C) 2012 GZ-ISCAS Inc., All Rights Reserved.
 */
package io.sugo.user.group.utils;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * 
 * @Description: 字符串相关的util类.  
 * @Author janpychou@qq.com
 * @CreateDate:   [May 26, 2015 7:40:17 PM]   
 *
 */
public class StringUtil {

    /**
     * 拷贝数组.
     * 如果strings为null,则返回null.
     * @param strings
     * @return
     * @since 5.0
     */
    public static String[] copy(String[] strings) {
        return strings == null ? null : Arrays.copyOf(strings, strings.length);
    }

    /**
     * 判断所有字符都是数字
     * @param string
     * @return
     * @since 5.0
     */
    public static boolean isAllNumber(String string) {
        for (int i = string.length(); --i >= 0;) {
            int chr = string.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     * 字节数组转换成16进制字符串。采用大写形式（0123456789ABCDEF）。
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制字符串转换成字节数组
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase(); //如果是大写形式
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexToBytes(String hexString) {
        if (hexString == null) {
            return null;
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }

        return bytes;
    }

    /**
     * 是否包含大写字符'A','B'...'Z'
     * @param string
     * @return
     */
    public static boolean containsUpperCase(String string) {

        if (string == null) {
            return false;
        }

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if ('A' <= ch && ch <= 'Z') {
                return true;
            }
        }

        return false;

    }

    /**
     * 截断字符串
     * @param string
     * @param maxLength
     * @return
     */
    public static String trim(String string, int maxLength) {
        if (string == null) {
            return null;
        }

        if (string.length() > maxLength) {
            return string.substring(0, maxLength);
        }

        return string;

    }

    public static Long parseId(Object o) {
        if (o instanceof String) {
            return Long.parseLong((String) o);
        }
        return (Long) o;
    }

    /**
     * 提取字符串中的数字
     */
    public static List<String> getNumbers(String string) {
        String regexString = "\\d+";
        return getMatchs(string, regexString);
    }

    /**
     * 获取匹配的字符串
     */
    public static List<String> getMatchs(String string, String regexString) {

        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(string);

        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(string.substring(matcher.start(), matcher.end()));
        }
        return list;
    }

    public static String replaceStrings(String string, String[] sources, String[] dests) {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            int matchedIndex = -1;
            for (int j = 0; j < sources.length; j++) {
                if (string.startsWith(sources[j], i)) {
                    matchedIndex = j;
                    break;
                }
            }

            if (matchedIndex == -1) {
                sb.append(string.charAt(i));
            } else {
                sb.append(dests[matchedIndex]);
                i += sources[matchedIndex].length() - 1;
            }
        }

        return sb.toString();
    }

    /**
     * 解析用逗号或分号分隔的数字
     * @param string
     * @return
     */
    public static List<Long> stringToLongs(String string) {
        List<Long> ret = new ArrayList<Long>();

        String[] strings = string.split(",|;");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null && strings[i].trim().length() > 0) {
                ret.add(toLong(strings[i]));
            }
        }

        return ret;
    }

    public static Long toLong(String string) {
        return string == null ? null : Long.parseLong(string.trim());
    }

    public static String seperate(List<?> list, String string) {
        if (list == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);

            if (i != 0) {
                builder.append(string);
            }
            if (element instanceof String) {
                builder.append(((String) element).trim());
            } else {
                builder.append(element);
            }
        }
        return builder.toString();
    }

    public static String seperate(Collection<?> list, String string) {
        if (list == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        int i = 0;
        if (list != null) {
            for (Object element : list) {

                if (i != 0) {
                    builder.append(string);
                }
                builder.append(element);

                i++;
            }
        }

        return builder.toString();
    }

    public static String seperateIgnoreNull(Collection<?> list, String string) {

        if (list == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        if (list != null) {
            for (Object element : list) {

                if (element != null) {
                    if (isFirst) {
                        builder.append(element);
                    } else {
                        builder.append(string).append(element);
                    }

                    isFirst = false;
                }
            }
        }

        return builder.toString();
    }

    public static String seperate(String[] strings, String string) {
        if (strings == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        int i = 0;
        if (strings != null) {
            for (String element : strings) {

                if (i != 0) {
                    builder.append(string);
                }
                builder.append(element.trim());

                i++;
            }
        }

        return builder.toString();
    }

    /**
     * 合并数组
     * @return
     */
    public static String combineArray(Object array, String seperator) {
        if (array == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Array.getLength(array); i++) {
            if (i == 0) {
                builder.append(Array.get(array, i));
            } else {
                builder.append(seperator).append(Array.get(array, i));
            }
        }

        return builder.toString();

    }

    /**
     * 格式化, 将字符串"{0}12{3}"中的"{0}", "{3}"替换成其他值.
     * 
     * @param string
     * @param replaceStrings
     * @return
     */
    public static String formatByMap(String string, Map<String, ?> map) {
        String regex = "\\{.*?\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String indexString = matcher.group();
            indexString = StringUtil.removeStartEnd(indexString, "{", "}");
            String replacement = String.valueOf(map.get(indexString));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();

    }

    /**
     * 格式化, 将字符串"{0}12{3}"中的"{0}", "{3}"替换成其他值.
     * 
     * @param string
     * @param replaceStrings
     * @return
     */
    public static String formatByNumber(StringConverter<Object> toString, String string, Object... replaceStrings) {
        String regex = "\\{\\d\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String indexString = matcher.group();
            indexString = StringUtil.removeStartEnd(indexString, "{", "}");
            int i = Integer.parseInt(indexString);
            if (i < replaceStrings.length) {
                String replacement = toString == null ? String.valueOf(replaceStrings[i]) : toString.toString(replaceStrings[i]);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();

    }

    /**
     * 格式化, 将字符串"{0}12{3}"中的"{0}", "{3}"替换成其他值.
     * 
     * @param string
     * @param replaceStrings
     * @return
     */
    public static String formatByNumber(String string, Object... replaceStrings) {
        String regex = "\\{\\d\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String indexString = matcher.group();
            indexString = StringUtil.removeStartEnd(indexString, "{", "}");
            int i = Integer.parseInt(indexString);
            if (i < replaceStrings.length) {
                String replacement = String.valueOf(replaceStrings[i]);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();

    }

    public static String removeStartEnd(String string, int startCount, int endCount) {
        return string.substring(startCount, string.length() - endCount);
    }

    /**
     * @since 5.0
     */
    public static String removeStart(String string, int startCount) {
        return string.substring(startCount);
    }

    /**
     * @since 5.0
     */
    public static String removeEnd(String string, int endCount) {
        return string.substring(0, string.length() - endCount);
    }

    public static String removeStartEnd(String string, String startString, String endString) {
        string = StringUtils.removeStart(string, startString);
        string = StringUtils.removeEnd(string, endString);
        return string;
    }

    /**
     * @since 5.0
     */
    public static String removeStart(String string, String startString) {
        string = StringUtils.removeStart(string, startString);
        return string;
    }

    /**
     * @since 5.0
     */
    public static String removeEnd(String string, String endString) {
        string = StringUtils.removeEnd(string, endString);
        return string;
    }

    /**
     * 半角转全角
     * 
     * @param input
     *            String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        if (input == null || input == "") {
            return "";
        }
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     * 
     * @param input
     *            String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        if (input == null || input == "") {
            return "";
        }
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

    /**
     * 将dtos中的attrName列用seperator连接成字符串返回.
     * <p>
     * 如dtos的内容为[{personId:1},{personId:2},{personId:3}]. seperator为",",
     * lable为"personId".则返回： "1,2,3".
     * <p>
     * 如果dtos为null,返回空字符串.
     * 
     * @param dtos
     * @param attrName
     * @param seperator
     * @return
     */
    public static String concatAttribute(Collection<?> dtos, String attrName, String seperator) {
        if (dtos == null) {
            return "";
        }
        ParamChecker.checkNull(attrName, "attrName");
        ParamChecker.checkNull(seperator, "seperator");
        ParamChecker.checkContainsNull(dtos, "dtos");
        try {
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Object valueItem : dtos) {
                if (first) {
                    first = false;
                } else {
                    sb.append(seperator);
                }
                sb.append(BeanUtils.getNestedProperty(valueItem, attrName));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将所给字符串按所给的seperator split成Long数组.示例：
     * <p>
     * splitToLong("1,2,3",",")后得到Long[]{1,2,3}.
     * 
     * @param str
     * @param seperator
     *            str
     * @return
     */
    public static Long[] splitToLong(String str, String seperator) {
        ParamChecker.checkNull(str, "str");
        ParamChecker.checkEmpty(seperator, "seperator");
        String[] strArray = str.split(seperator);
        Long[] result = new Long[strArray.length];
        for (int i = 0; i < result.length; i++) {
            if (ParamChecker.isEmpty(strArray[i])) {
                result[i] = null;
            } else {
                result[i] = Long.valueOf(strArray[i]);
            }
        }
        return result;
    }

    /**
     * 将所给字符串按所给的seperator split成List.
     * @param str
     * @param seperator
     * @return
     */
    public static List<String> splitToList(String str, String seperator) {
        ParamChecker.checkNull(str, "str");
        ParamChecker.checkEmpty(seperator, "seperator");
        List<String> splitString = new ArrayList<String>();
        for (String temp : str.split(seperator)) {
            splitString.add(temp.trim());
        }
        return splitString;
    }

    public static String[] split(String str, String split) {
        List<String> strings = new ArrayList<String>(16);
        StringTokenizer token = new StringTokenizer(str, split, false);
        while (token.hasMoreElements()) {
            strings.add(token.nextToken());
        }
        String[] strArray = new String[strings.size()];
        return strings.toArray(strArray);
    }

    public static List<String> split2List(String str, String split) {
        List<String> strings = new ArrayList<String>(16);
        StringTokenizer token = new StringTokenizer(str, split);
        while (token.hasMoreElements()) {
            strings.add(token.nextToken());
        }
        return strings;
    }

    /**
     * 首字母大写.
     * @param target
     * @return
     */
    public static String firstLetterUpper(String target) {
        return Character.toUpperCase(target.charAt(0)) + target.substring(1);
    }

    /**
     * 首字母小写
     * 
     * @param target
     * @return
     */
    public static String firstLetterLower(String target) {
        return Character.toLowerCase(target.charAt(0)) + target.substring(1);
    }

    public static String nullToEmpty(Object string) {
        return string == null ? "" : string.toString();
    }

    public static String replace(String string, Map<String, String> map) {
        int count = string.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {

            boolean isReplaced = false;
            Set<Map.Entry<String, String>> entrySet = (Set<Map.Entry<String, String>>) map.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (string.startsWith(entry.getKey(), i)) {
                    builder.append(entry.getValue());
                    isReplaced = true;
                    i += (entry.getKey().length() - 1);
                    break;
                }
            }

            if (!isReplaced) {
                builder.append(string.charAt(i));
            }

        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public static String replaceSqlSpecialChars(String string) {
        if (string == null) {
            return null;
        }

        Map<String, String> map = toMap("%", "\\%", "_", "\\_");
        return replace(string, map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map toMap(Object... objects) {
        Map map = new HashMap();
        for (int i = 0; i < objects.length; i += 2) {
            map.put(objects[i], objects[i + 1]);
        }
        return map;
    }

    /**
     * 不为null，也不是全是空白字符
     */
    public static boolean isNotNullAndNotEmpty(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static boolean isNullOrEmpty(String string) {
        return !isNotNullAndNotEmpty(string);
    }

    /**
     * @since 5.0
     */
    public static boolean isNotEmpty(String[] strings) {
        return !isEmpty(strings);
    }

    /**
     * @since 5.0
     */
    public static boolean isEmpty(String[] strings) {
        return strings == null || strings.length == 0;
    }

    public static interface StringConverter<T> {
        public String toString(T value);
    }

    public static class DefaultStringConverter implements StringConverter<Object> {

        private NumberFormat numberFormat;
        private FastDateFormat dateFormat;

        public DefaultStringConverter() {

            numberFormat = new DecimalFormat("0.#");
            dateFormat = FastDateFormat.getInstance("yyy-MM-dd");

        }

        @Override
        public String toString(Object value) {

            if (value == null) {
                return "";
            }

            if (value instanceof Double || value instanceof Float) {
                return numberFormat.format(value);
            } else if (value instanceof Date || value instanceof Calendar) {
                return dateFormat.format(value);
            }

            return value.toString();

        }
    }

    /**
     * 
     * @Description: 判断字符串是否是null或者是"",不去除前后空格
     * @author chenjianbin
     * @version 2014-2-26 下午02:56:07 
     * @param string
     * @return
     *
     */
    public static boolean isEmptyString(String string) {
        if (string == null || string.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串不是null并且不是"",不去除前后空格
     */
    public static boolean isNotEmptyString(String string) {
        return !isEmptyString(string);
    }

    /**
     * 判断是否包含
     * @param array
     * @param string
     * @return
     * @since 5.0
     */
    public static boolean contains(String[] array, String string) {
        return Arrays.asList(array).contains(string);
    }

    public static int[] toInts(String[] values) {

        int[] result = new int[values.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(values[i].trim());
        }

        return result;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public static Map<String, String> parseArgs(String argvStart, String[] args) {
        Map<String, String> rs = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String v = ((i < args.length - 1) && !args[i + 1].startsWith(argvStart)) ? args[++i] : null;
            arg = arg.substring(argvStart.length());
            rs.put(arg, v);
        }
        return rs;
    }

}
