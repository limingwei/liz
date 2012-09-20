package li.aop;

import li.annotation.Bean;
import li.aop.AopChain;
import li.aop.AopFilter;

@Bean
public class LogFilter implements AopFilter {
	public void filter(AopChain chain) {
		System.err.println("log before");
		chain.doChain();
		System.err.println("log after");
	}
}