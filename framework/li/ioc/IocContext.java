package li.ioc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import li.aop.AopFilter;
import li.aop.AopInterceptor;
import li.model.Bean;
import li.model.Field;
import li.util.Convert;
import li.util.Files;
import li.util.Log;
import li.util.Reflect;
import li.util.Verify;

/**
 * Ioc容器,保存所有的Bean
 * 
 * @author li (limw@w.cn)
 * @version 0.1.5 (2012-05-08)
 * @see li.ioc.Ioc
 */
public class IocContext {
	private static final Log log = Log.init();

	/**
	 * List,用于保存所有的BEAN
	 */
	public final List<Bean> BEANS = new ArrayList<Bean>();

	/**
	 * 存储IocContext的实例,它会是单例的
	 */
	private static IocContext IOC_CONTEXT = null;

	/**
	 * 私有的构造方法,保证IocContext是单例的
	 */
	private IocContext() {
		log.debug("new IocContext()");
	}

	/**
	 * 得到一个单例的IocContext对象,包含通过不同方式配置的Bean集合,在List<Bean> BEANS里面
	 */
	public static synchronized IocContext getInstance() {
		if (IOC_CONTEXT == null) {
			Log.put("IOCSTARTUP", System.currentTimeMillis());

			IOC_CONTEXT = new IocContext();

			// STEP-1-使用XmlIocLoader和AnnotationIocLoader初始化IocContext,即添加Beans
			IOC_CONTEXT.BEANS.addAll(new XmlIocLoader().getBeans());
			IOC_CONTEXT.BEANS.addAll(new AnnotationIocLoader().getBeans());

			// STEP-2-处理field.value中得 ${name}
			Properties properties = Files.load("config.properties");
			for (Bean bean : IOC_CONTEXT.BEANS) {
				for (Field field : bean.fields) {// 如果 filed.value 形如 ${name} 则使用 properties 中key为name的值替换
					if (field.value.length() > 3 && field.value.startsWith("${") && field.value.endsWith("}")) {
						field.value = properties.getProperty(field.value.replace("${", "").replace("}", ""));
					}
				}
			}

			// STEP-3-实例化所有的AopFilter,并缓存之
			for (Bean bean : IOC_CONTEXT.BEANS) {
				if (AopFilter.class.isAssignableFrom(bean.type)) {
					bean.instance = Reflect.born(bean.type);
				}
			}

			// STEP-4-实例化并Aop化所有的非AopFilter的Bean,并缓存之
			for (Bean bean : IOC_CONTEXT.BEANS) {
				if (!AopFilter.class.isAssignableFrom(bean.type)) {
					try {
						bean.instance = AopInterceptor.getInstance(bean.type);
					} catch (Throwable e) {
						log.debug("Aop not supported: " + e);
						bean.instance = Reflect.born(bean.type);
					}
				}
			}

			// STEP-5-给IocContext中的Bean设置属性
			for (Bean bean : IOC_CONTEXT.BEANS) {
				for (Field field : bean.fields) {
					log.info(String.format("SET Field: %s %s -> %s", field.name, field.value, bean.type.getName()));
					if (Verify.basicType(field.type)) {// 基本类型,直接设值
						Reflect.set(bean.instance, field.name, Convert.toType(field.type, field.value));
					} else {// 非基本类型,设为相应的bean
						Reflect.set(bean.instance, field.name, Ioc.get(field.type, field.value));
					}
				}
			}

			log.debug(String.format("%s beans started up in %sms", IOC_CONTEXT.BEANS.size(), System.currentTimeMillis() - (Long) Log.get("IOCSTARTUP")));
		}
		return IOC_CONTEXT;
	}
}