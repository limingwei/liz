package li.aop;

import org.junit.Test;

import li.ioc.Ioc;
import li.test.BaseTest;

public class AopTest extends BaseTest {

	@Test
	public void testAop() {
		final Account account = Ioc.get(Account.class);
		for (int i = 0; i < 10; i++) {
			new Thread() {
				public void run() {
//					System.out.println("hello");
					account.list(null);
				};
			}.start();
		}
	}
}