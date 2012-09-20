package li.aop;

import li.dao.Trans;

/**
 * 一个内置的AopFilter
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-09-20)
 */
public class TransFilter implements AopFilter {
	public void filter(final AopChain chain) {
		new Trans() {
			public void run() {
				chain.doChain();
			}
		};
	}
}