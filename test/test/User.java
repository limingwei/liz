package test;

import li.annotation.Aop;
import li.annotation.Bean;
import li.annotation.Trans;

@Bean
public class User {

	@Aop(LogFilter.class)
	@Trans
	public String sayHi(String msg1, String msg2) {
		System.out.println("user say hi");
		return msg1 + "\t" + msg2;
	}
}