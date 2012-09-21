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

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			Thread.sleep(300);
			new Thread() {
				public void run() {
					final Account account = Ioc.get(Account.class);
					account.list(null);
				}
			}.start();
		}
	}
}