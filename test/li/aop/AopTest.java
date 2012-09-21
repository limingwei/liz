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

	public static void main(String[] args) {
		new Thread() {
			public void run() {
				final Account account = Ioc.get(Account.class);
				account.list(null);
			}
		}.start();
	}
}