package li.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志工具类,自动适配Log4j或Console
 * 
 * @author li (limw@w.cn)
 * @version 0.1.6 (2012-07-05)
 */
public abstract class Log {
	/**
	 * Log初始化方法,自动适配Log4j或Console
	 */
	public static Log init(final Class<?> type) {
		try {
			return new Log() {// 尝试初始化Log4J
				Object logger = Class.forName("org.apache.log4j.Logger").getMethod("getLogger", Class.class).invoke(null, type);

				protected void log(String method, Object msg) {
					try {
						StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
						logger.getClass().getMethod(method, Object.class).invoke(logger, String.format("%s() #%s %s", trace.getMethodName(), trace.getLineNumber(), msg));
					} catch (Exception e) {
						throw new RuntimeException("Exception at li.util.Log.init().new Log() {}.log(String, Object)", e);
					}
				};
			};
		} catch (Exception e) {
			return new Log() {// 返回ConsoleLog
				protected void log(String method, Object msg) {
					StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
					if (method.toUpperCase().equals("ERROR") || method.toUpperCase().equals("FATAL")) {
						System.err.println(String.format("%s: %s.%s() #%s %s", method.toUpperCase(), type.getName(), trace.getMethodName(), trace.getLineNumber(), msg));
					} else {
						System.out.println(String.format("%s: %s.%s() #%s %s", method.toUpperCase(), type.getName(), trace.getMethodName(), trace.getLineNumber(), msg));
					}
				}
			};
		}
	}

	/**
	 * 根据类名初始化Log
	 */
	public static Log init(String className) {
		return init(Reflect.getType(className));
	}

	/**
	 * 初始化Log最简单的方法,会自动获取调用者的类型
	 */
	public static Log init() {
		return init(Thread.currentThread().getStackTrace()[2].getClassName());
	}

	/**
	 * 抽象方法,由不同的Log做具体的适配
	 */
	protected abstract void log(String method, Object msg);

	/**
	 * 输出INFO级别的日志
	 */
	public void info(Object msg) {
		log("info", msg);
	}

	/**
	 * 输出ERROR级别的日志
	 */
	public void error(Object msg) {
		log("error", msg);
	}

	/**
	 * 输出DEBUG级别的日志
	 */
	public void debug(Object msg) {
		log("debug", msg);
	}

	/**
	 * 输出FATAL级别的日志
	 */
	public void fatal(Object msg) {
		log("fatal", msg);
	}

	/**
	 * 输出TRACE级别的日志
	 */
	public void trace(Object msg) {
		log("trace", msg);
	}

	/**
	 * 输出WARN级别的日志
	 */
	public void warn(Object msg) {
		log("warn", msg);
	}

	/**
	 * 一个缓存,可用于暂时保存一个值
	 */
	private static final Map<String, Object> LOG_MAP = new HashMap<String, Object>();

	/**
	 * 向LOG_MAP中设值,synchronized方法
	 */
	public synchronized static void put(String key, Object value) {
		LOG_MAP.put(key, value);
	}

	/**
	 * 从LOG_MAP中取值,synchronized方法
	 */
	public synchronized static Object get(String key) {
		return LOG_MAP.get(key);
	}
}