package li.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import li.annotation.Aop;
import li.annotation.Trans;
import li.ioc.Ioc;
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
	 * Aop包裹一个对象
	 */
	public Object getInstance(Object target) {
		filtersMap(target.getClass());
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		return enhancer.create();
	}

	/**
	 * 代理执行方法
	 */
	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return new AopChain(target, method, args, filtersMap.get(method), proxy).doFilter().getResult();
	}

	/**
	 * 搜集指定类型所有方法的AopFilter
	 */
	private void filtersMap(Class<?> type) {
		for (Method method : type.getDeclaredMethods()) {
			List<AopFilter> filters = new ArrayList<>();
			Aop aop = method.getAnnotation(Aop.class);
			if (null != aop) {
				for (Class<? extends AopFilter> filterType : aop.value()) {
					AopFilter filter = Ioc.get(filterType);
					filters.add(null == filter ? Reflect.born(filterType) : filter);
				}
			}
			if (null != method.getAnnotation(Trans.class)) {
				filters.add(TRANS_FILTER);
			}
			filtersMap.put(method, filters);
		}
	}
}