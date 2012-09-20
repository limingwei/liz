package li.aop;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

/**
 * Aop方法执行链
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-09-20)
 */
public class AopChain {
	/**
	 * 目标对象
	 */
	private Object target;
	/**
	 * 方法对象
	 */
	private Method method;
	/**
	 * 参数数组
	 */
	private Object[] args;
	/**
	 * 方法返回值
	 */
	private Object result;
	/**
	 * AopFilter列表
	 */
	private List<AopFilter> filters;
	/**
	 * 方法代理
	 */
	private MethodProxy proxy;
	/**
	 * AopFilter索引
	 */
	private int index = 0;

	public AopChain(Object target, Method method, Object[] args, List<AopFilter> filters, MethodProxy proxy) {
		this.target = target;
		this.method = method;
		this.args = args;
		this.filters = filters;
		this.proxy = proxy;
	}

	public AopChain doChain() {
		if (null == filters || index == filters.size()) {
			try {
				this.result = proxy.invokeSuper(target, args);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} else {
			index++;
			filters.get(index - 1).filter(this);
		}
		return this;
	}

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}