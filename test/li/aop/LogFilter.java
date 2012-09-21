package li.aop;

import li.annotation.Bean;
import li.aop.AopChain;
import li.aop.AopFilter;

@Bean
public class LogFilter implements AopFilter {
	public void doFilter(AopChain chain) {
		System.err.println("log before");
		chain.doFilter();
		System.err.println("log after");
	}
}