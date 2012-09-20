package li.ioc;

import li.annotation.Bean;
import li.annotation.Inject;

@Bean
public class A {
	@Inject
	public A a;
	@Inject
	public B b;
	@Inject("123")
	public int num;

	public String sayHi(String name) {
		System.out.println("方法被执行 \t name=" + name);
		return "本来的返回值 \t name=" + name;
	}
}