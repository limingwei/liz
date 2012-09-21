package li.aop;

import li.dao.Trans;

/**
 * 一个内置的AopFilter
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-09-20)
 */
public class TransFilter implements AopFilter {
	/**
	 * 使用一个li.dao.Trans包裹执行目标方法
	 */
	public void doFilter(final AopChain chain) {
		new Trans() {
			public void run() {
				chain.doFilter();
			}
		};
	}
}