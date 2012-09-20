package li.aop;

import java.lang.reflect.Method;
import java.util.List;

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
	 * AopFilter索引
	 */
	private int index = 0;

	public AopChain(Object target, Method method, Object[] args, List<AopFilter> filters) {
		this.target = target;
		this.method = method;
		this.args = args;
		this.filters = filters;
	}

	public AopChain doChain() throws Exception {
		if (index == filters.size()) {
			this.result = method.invoke(target, args);
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