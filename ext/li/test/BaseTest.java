package li.test;

import java.lang.reflect.Field;

import li.annotation.Inject;
import li.ioc.Ioc;
import li.util.Page;
import li.util.Reflect;

/**
 * 测试类的基类，会为每一个@Inject注解的字段设值
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-07-21)
 */
public class BaseTest {
	/**
	 * 模拟的 li.dao.Page
	 */
	protected Page page = new Page();

	/**
	 * 初始化时为每一个@Inject注解的属性设值
	 */
	public BaseTest() {
		for (Field field : getClass().getDeclaredFields()) {
			Inject inject = field.getAnnotation(Inject.class);
			if (null != inject) {
				Reflect.set(this, field.getName(), Ioc.get(field.getType(), inject.value()));
			}
		}
	}
}