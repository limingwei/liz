package li.aop;

import org.junit.Test;

import li.ioc.Ioc;
import li.test.BaseTest;

public class AopTest extends BaseTest {

	@Test
	public void testAop() {
		User user = Ioc.get(User.class);
		Account account = Ioc.get(Account.class);
		System.out.println(user);
		account.list(null);
	}
}