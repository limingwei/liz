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
	private Map<Method, List<AopFilter>> filtersMap = new HashMap<>();

	private static final TransFilter TRANS_FILTER = new TransFilter();

	/**
	 * Aop包裹一个对象
	 */
	public <T> T getInstance(T target) {
		for (Method method : target.getClass().getDeclaredMethods()) {
			List<AopFilter> filters = new ArrayList<>();
			Aop aop = method.getAnnotation(Aop.class);
			if (null != aop) {
				for (Class<? extends AopFilter> type : aop.value()) {
					AopFilter filter = Ioc.get(type);
					filters.add(null == filter ? Reflect.born(type) : filter);
				}
			}
			if (null != method.getAnnotation(Trans.class)) {
				filters.add(TRANS_FILTER);
			}
			filtersMap.put(method, filters);
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		return (T) enhancer.create();
	}

	/**
	 * 代理执行方法
	 */
	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		AopChain chain = new AopChain(target, method, args, filtersMap.get(method), proxy);
		return chain.doChain().getResult();
	}
}