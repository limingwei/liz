package li.aop;

import org.junit.Test;

import li.ioc.Ioc;
import li.test.BaseTest;

public class AopTest extends BaseTest {

	@Test
	public void testAop() {
		final Account account = Ioc.get(Account.class);
		account.list(null);
	}

	@Test
	public void testAop2() {
		User user = Ioc.get(User.class);
		user.sayHi("abc", "xyz");
	}

	public static void main(String[] args) throws Exception {

		final Account account = Ioc.get(Account.class);
		account.list(null);

		for (int i = 0; i < 100; i++) {
			Thread.sleep(300);
			new Thread() {
				public void run() {
					for (; true;) {
						final Account account = Ioc.get(Account.class);
						account.list(null);
					}
				}
			}.start();
		}
	}
}