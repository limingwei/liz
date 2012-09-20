package li.ioc;

import li.annotation.Bean;
import li.annotation.Inject;

@Bean
public class B {
	@Inject
	private A a;

	@Inject
	private B b;
}