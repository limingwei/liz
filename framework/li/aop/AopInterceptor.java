package li.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import li.annotation.Aop;
import li.annotation.Trans;
import li.util.Reflect;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Aop包裹类
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-09-20)
 */
public class AopInterceptor implements MethodInterceptor {
	/**
	 * 保存当前对象各方法的AopFilters
	 */
	private Map<Method, List<AopFilter>> filtersMap = new HashMap<>();
	/**
	 * 内置的TransFilter的一个实例
	 */
	private static final TransFilter TRANS_FILTER = new TransFilter();

	/**
	 * 搜集指定类型所有方法的AopFilter
	 */
	private void filtersMap(Class<?> type) {
		for (Method method : type.getDeclaredMethods()) {// 对每一个方法
			List<AopFilter> filters = new ArrayList<>();
			Aop aop = method.getAnnotation(Aop.class);// 如果有@Aop注解
			if (null != aop) {
				for (Class<? extends AopFilter> filterType : aop.value()) {// 对每一个@Aop.value()的值
					filters.add(Reflect.born(filterType));// 实例化这个AopFilter并加入到列表,用Ioc管理AopFilter失败
				}
			}
			if (null != method.getAnnotation(Trans.class)) {// 如果有@Trans注解
				filters.add(TRANS_FILTER);
			}
			filtersMap.put(method, filters);
		}
	}

	/**
	 * Aop包裹一个对象
	 */
	public Object getInstance(Object target) {
		filtersMap(target.getClass());// 构造这个对象类型所有方法的AopFilter集合
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		return enhancer.create();
	}

	/**
	 * 代理执行方法
	 */
	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return new AopChain(target, method, args, filtersMap.get(method), proxy).doFilter().getResult();// 使用AopChian代理执行这个方法并返回值
	}
}