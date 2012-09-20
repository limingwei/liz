package li.util;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import li.dao.Record;
import li.model.Field;

/**
 * 类型转换的工具类
 * 
 * @author li (limw@w.cn)
 * @version 0.1.7 (2012-05-08)
 */
public class Convert {
	/**
	 * 将数据对象或对象的集合或数组转换为json
	 */
	public static String toJson(Object target) {
		if (target instanceof Collection) {// 如果是集合,转换成数组处理
			return toJson(((Collection<?>) target).toArray());
		}
		if (target.getClass().isArray()) {// 如果是数组
			String json = "[";
			for (Object one : (Object[]) target) {// 处理每个对象
				json += toJson(one) + ",";
			}
			return json.substring(0, json.length() - 1) + "]";// 返回
		}
		String json = "{";// 处理单个对象
		if (Record.class.isAssignableFrom(target.getClass())) {// 如果是Record
			for (Entry<String, Object> entry : ((Record<?>) target).entrySet()) {// Record的每个属性
				json += "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
			}
		} else {// 不是Record，按照POJO处理
			for (Field field : Field.list(target.getClass(), true)) {// POJO的每个属性
				json += "\"" + field.name + "\":\"" + Reflect.get(target, field.name) + "\",";
			}
		}
		return json.substring(0, json.length() - 1) + "}";
	}

	/**
	 * 将json转换为数据对象的List
	 */
	public static <T> List<T> fromJson(Class<T> type, String json) {
		final String JSON_REGEX = "^.*}[]]{0,1},[\\[]{0,1}\\{.*$", JSON_SPLIT = "}[]]{0,1},[\\[]{0,1}\\{";
		List<T> list = new ArrayList<T>();
		if (Verify.regex(json, JSON_REGEX)) {// 处理多个对象
			for (String one : json.split(JSON_SPLIT)) {
				list.addAll(fromJson(type, one)); // 这里递归调用
			}
			return (List<T>) list;// 返回
		}
		T one = Reflect.born(type);// 处理单个对象
		for (String field : json.split(",")) {
			String[] strs = field.split(":");
			String key = strs[0].substring(strs[0].indexOf('"') + 1, strs[0].lastIndexOf('"'));
			String value = strs[1].substring(strs[1].indexOf('"') + 1, strs[1].lastIndexOf('"'));
			Reflect.set(one, key, value);// 存在这个属性并不为空字符串且不为null
		}
		list.add(one);
		return list;
	}

	/**
	 * 将数组转换为Map,奇数位为key,偶数位为value; items必须为偶数个
	 */
	public static Map<Object, Object> toMap(Object... items) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (null != items && items.length > 0) {// 非空判断
			if (items.length % 2 != 0) {
				throw new RuntimeException("Count of items must be even !!!");// 个数为奇数,抛出异常
			} else {
				for (int i = 0; i < items.length; i = i + 2) {
					map.put(items[i], items[i + 1]);
				}
			}
		}
		return map;
	}

	/**
	 * 把字符串用一次MD5加密后返回
	 */
	public static String toMD5(Object input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(input.toString().getBytes());
			byte[] byteDigest = messageDigest.digest();
			StringBuffer stringBuffer = new StringBuffer("");
			int i;
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset] < 0 ? byteDigest[offset] + 256 : byteDigest[offset];
				if (i < 16) {
					stringBuffer.append("0");
				}
				stringBuffer.append(Integer.toHexString(i));
			}
			return stringBuffer.toString(); // 32位加密
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.util.Convert.toMD5(Object)", e);
		}
	}

	/**
	 * 把传入的value转换为type类型
	 */
	public static <T> T toType(Class<T> type, Object value) {
		if ((null != type && null != value) && ((type.equals(Integer.TYPE)) || (type.equals(Integer.class)))) { // 两参数均不为空,且type为Integer类型
			return (T) Integer.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Boolean.TYPE)) || (type.equals(Boolean.class)))) {
			return (T) Boolean.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Long.TYPE)) || (type.equals(Long.class)))) {
			return (T) Long.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Float.TYPE)) || (type.equals(Float.class)))) {
			return (T) Float.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Double.TYPE)) || (type.equals(Double.class)))) {
			return (T) Double.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Short.TYPE)) || (type.equals(Short.class)))) {
			return (T) Short.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Byte.TYPE)) || (type.equals(Byte.class)))) {
			return (T) Byte.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && ((type.equals(Character.TYPE)) || (type.equals(Character.class)))) {
			return (T) Character.valueOf(value.toString().charAt(0));
		} else if ((null != type && null != value) && (type.equals(java.util.Date.class))) {
			return (T) java.sql.Date.valueOf(value.toString().trim());// *****这里也许有更好的处理方式
		} else if ((null != type && null != value) && (type.equals(java.sql.Date.class))) {
			return (T) java.sql.Date.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && (type.equals(java.sql.Time.class))) {
			return (T) java.sql.Time.valueOf(value.toString().trim());
		} else if ((null != type && null != value) && (type.equals(java.sql.Timestamp.class))) {
			return (T) java.sql.Timestamp.valueOf(value.toString().trim());
		} else {
			return (T) value;
		}
	}

	/**
	 * 将数组中的每个元素进行类型转换
	 * 
	 * @param type type不能是基本数据类型 Primitive
	 */
	public static <T> T[] toType(Class<T> type, Object... values) {
		T[] dest = null;
		if (null != type && null != values && values.length > 0) {// 参数不为空且数组大小不为0
			if (type == Integer.TYPE) {
				type = (Class<T>) Integer.class;// 将原始数据类型转换为其封装类型
			} else if (type == Boolean.TYPE) {
				type = (Class<T>) Boolean.class;
			} else if (type == Long.TYPE) {
				type = (Class<T>) Long.class;
			} else if (type == Float.TYPE) {
				type = (Class<T>) Float.class;
			} else if (type == Double.TYPE) {
				type = (Class<T>) Double.class;
			} else if (type == Short.TYPE) {
				type = (Class<T>) Short.class;
			} else if (type == Byte.TYPE) {
				type = (Class<T>) Byte.class;
			} else if (type == Character.TYPE) {
				type = (Class<T>) Character.class;
			}
			dest = (T[]) Array.newInstance(type, values.length);// 生成目标类型数组
			for (int i = 0; i < values.length; i++) {
				dest[i] = (T) toType(type, values[i]);// 类型转换每一个元素
			}
		}
		return dest;
	}
}