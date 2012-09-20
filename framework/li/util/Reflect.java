package li.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import li.dao.Record;

/**
 * 反射工具类,封装了一些反射方法
 * 
 * @author li (limw@w.cn)
 * @version 0.1.4 (2012-05-08)
 */
public class Reflect {
	/**
	 * 根据传入的类名返回对应的Class对象
	 */
	public static <T> Class<T> getType(String type) {
		try {
			return (Class<T>) Class.forName(type);
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.util.Reflect.getType(String)", e);
		}
	}

	/**
	 * 构造一个新的实例,根据类型,参数类型列表和参数列表
	 */
	public static <T> T born(Class<T> type, Class<?>[] argTypes, Object[] args) {
		try {
			return type.getConstructor(argTypes).newInstance(args);
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.util.Reflect.born(Class<T>, Class<?>[], Object[])", e);
		}
	}

	/**
	 * 构造一个新的实例,根据类型和参数列表
	 */
	public static <T> T born(Class<T> type, Object... args) {
		return born(type, typesOf(args), args);
	}

	/**
	 * 构造一个新的实例,根据类型名,参数类型列表和参数列表
	 */
	public static <T> T born(String type, Class<?>[] argTypes, Object[] args) {
		return (T) born(getType(type), argTypes, args);
	}

	/**
	 * 构造一个新的实例,根据类型名和参数列表
	 */
	public static <T> T born(String type, Object... args) {
		return (T) born(type, typesOf(args), args);
	}

	/**
	 * 得到一个方法,根据对象类型,方法名和参数类型列表
	 */
	public static Method getMethod(Class<?> targetType, String methodName, Class<?>... argTypes) {
		try {
			Method method = targetType.getDeclaredMethod(methodName, argTypes);// 在当前类型中查找方法
			method.setAccessible(true);// 设置可见性为true
			return method;
		} catch (Exception e) {// 在超类型中查找方法
			return Object.class == targetType.getSuperclass() ? null : getMethod(targetType.getSuperclass(), methodName, argTypes);
		}
	}

	/**
	 * 执行target的method方法,以args为参数
	 */
	public static Object invoke(Object target, Method method, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (Exception e) {
			throw new RuntimeException("method invoking Exception", e);
		}
	}

	/**
	 * 执行target的methodName方法,args类型需要顺序匹配于argTypes
	 */
	public static Object invoke(Object target, String methodName, Class<?>[] argTypes, Object[] args) {
		return invoke(target, getMethod(target.getClass(), methodName, argTypes), args);
	}

	/**
	 * 执行target的methodName方法,args为参数列表,不可以为null
	 * 
	 * @param args 可以没有,表示方法无参数,不可以为null
	 */
	public static Object invoke(Object target, String methodName, Object... args) {
		return invoke(target, getMethod(target.getClass(), methodName, typesOf(args)), args);
	}

	/**
	 * 执行targetType类型的methodName静态方法,args类型需要顺序匹配于argTypes
	 */
	public static Object call(String targetType, String methodName, Class<?>[] argTypes, Object[] args) {
		return invoke(null, getMethod(getType(targetType), methodName, argTypes), args);
	}

	/**
	 * 执行targetType类型的methodName静态方法,args为参数列表,不可以为null
	 */
	public static Object call(String targetType, String methodName, Object... args) {
		return invoke(null, getMethod(getType(targetType), methodName, typesOf(args)), args);
	}

	/**
	 * 得到一个targetType的名为fieldName的属性,或者它的超类的
	 */
	public static Field getField(Class<?> targetType, String fieldName) {
		try {
			Field field = targetType.getDeclaredField(fieldName);// 在当前类型中得到属性
			field.setAccessible(true);// 设置可操作性为true
			return field;
		} catch (Exception e) {// 如果当前类型中无这个属性,则从其超类中查找
			return Object.class == targetType.getSuperclass() ? null : getField(targetType.getSuperclass(), fieldName);
		}
	}

	/**
	 * 探测一个属性的类型,从Field或者Getter
	 */
	public static Class<?> fieldType(Class<?> targetType, String fieldName) {
		Field field = getField(targetType, fieldName);
		if (null != field) { // 从field探测
			return field.getType();
		}
		String method = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method getter = getMethod(targetType, method);
		if (null != getter) {// 从getter方法探测
			return getter.getReturnType();
		}
		return null;
	}

	/**
	 * 返回target的名为fieldName的属性的值,优先采用Getter方法,Field字段其次
	 */
	public static Object get(Object target, String fieldName) {
		try {
			String method = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			Method getter = getMethod(target.getClass(), method);
			return getter.invoke(target);// 使用Getter方法
		} catch (Exception e) {// 没有匹配的Getter方法
			try {
				return getField(target.getClass(), fieldName).get(target);// 通过属性访问
			} catch (Exception ex) {
				if (target instanceof Record<?>) {
					return ((Record<?>) target).get(fieldName);// 通过Record.get()方法
				} else {
					throw new RuntimeException(String.format("Reflect.get() target=%s,fieldName=%s", target, fieldName));
				}
			}
		}
	}

	/**
	 * 设置 target的名为fieldName的属性的值为 value,优先采用Setter方法,Field字段其次
	 */
	public static void set(Object target, String fieldName, Object value) {
		try {
			String method = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			Method setter = getMethod(target.getClass(), method, fieldType(target.getClass(), fieldName));
			setter.invoke(target, value);// 使用Setter方法,这里没做类型转换
		} catch (Exception e) {// 没有匹配的Setter方法
			try {
				Field field = getField(target.getClass(), fieldName);
				field.set(target, Convert.toType(field.getType(), value));// 通过属性访问,这里有做类型转换
			} catch (Exception ex) {
				if (target instanceof Record<?>) {
					((Record<?>) target).set(fieldName, value);// 通过Record.set()方法,这里也没做类型转换
				} else {
					throw new RuntimeException(String.format("Reflect.set() target=%s,fieldName=%s,value=%s", target, fieldName, value));
				}
			}
		}
	}

	/**
	 * 返回一些对象的类型数组
	 */
	public static Class<?>[] typesOf(Object... objects) {
		Class<?>[] types = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			types[i] = objects[i].getClass();
		}
		return types;
	}

	/**
	 * 进行对象属性浅层复制
	 * 
	 * @param src 源对象
	 * @param dest 目的对象
	 * @return 返回目的对象
	 */
	public static <T> T copy(Object src, T dest) {
		for (li.model.Field attribute : li.model.Field.list(dest.getClass(), false)) {// 迭代目标对象中的每一个属性
			Field field = getField(src.getClass(), attribute.name);
			if (null != field && !Modifier.isFinal(field.getModifiers())) {// 如果两个对象中均有此属性,且目标对象中的此属性可写
				set(dest, attribute.name, get(src, attribute.name));
			}
		}
		return dest;
	}

	/**
	 * 超类的第index个泛型参数在子类中的实际类型
	 * 
	 * @param subType 子类类型
	 * @param index 泛型参数索引
	 */
	public static Type actualType(Class<?> subType, Integer index) {
		try {
			return ((ParameterizedType) subType.getGenericSuperclass()).getActualTypeArguments()[index];
		} catch (Exception e) {// 不能转换为ParameterizedType或者数组越界的异常,探测他的超类
			return Object.class == subType.getSuperclass() ? null : actualType(subType.getSuperclass(), index);
		}
	}

	/**
	 * 得到一个方法的参数注解列表
	 */
	public static <T extends Annotation> T[] argAnnotations(Method method, Class<T> annotation) {
		T[] array = (T[]) Array.newInstance(annotation, method.getParameterTypes().length);// 生成注解数组
		Annotation[][] ats = method.getParameterAnnotations();// 所有参数注解的二维数组
		for (int i = 0; i < ats.length; i++) {// 每一个参数的注解数组
			for (Annotation at : ats[i]) {// 一个参数上的每一个注解
				if (annotation.isAssignableFrom(at.annotationType())) {// 如果注解类型是指定类型
					array[i] = (T) at;
					break;// 跳出当前层循环,处理下一个参数
				}
			}
		}
		return array;
	}

	/**
	 * 获得传入的方法的形参名列表
	 */
	public static String[] argNames(Method method) {
		return ParamScaner.getParameterNames(method);
	}
}