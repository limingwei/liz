package li.aop;

import li.annotation.Bean;
import li.annotation.Inject;
import li.aop.AopChain;
import li.aop.AopFilter;

@Bean
public class LogFilter implements AopFilter {
	@Inject("##############")
	private String msg;

	public void doFilter(AopChain chain) {
		System.err.println("log before " + msg);
		chain.doFilter();
		System.err.println("log after");
	}
}