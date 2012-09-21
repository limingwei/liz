package li.dao;

import li.annotation.Inject;
import li.test.BaseTest;
import li.util.Convert;

import org.junit.Test;

import demo.record.Account;

public class TransTest extends BaseTest {
	@Inject
	Account accountDao;

	@Inject
	User userDao;

	@Test
	public void testMultipleTrans() {
		userDao.testMultipleTrans2();
	}

	@Test
	public void testPassValue() {
		User user = (User) new Trans(false) {
			public void run() {
				userDao.update("SET email=:email WHERE username=:username", map());
				map().put("user", userDao.find("WHERE username=?", map().get(":username")));
			}
		}.set(Convert.toMap(":email", "tom@w.cn", ":username", "xiaoming")).go().map().get("user");

		Boolean flag = new Trans() {
			public void run() {
				userDao.update("SET email='li@w.cn' WHERE 1=2", map());
			}
		}.success();

		System.err.println(user);
		System.err.println(flag);
	}

	@Test
	public void test1() {
		System.err.println(new Trans(false) {
			public void run() {
				userDao.update(new User().set("id", 2).set("username", "u-5" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1"));
				userDao.update(new User().set("username", "u-4" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1"));
				map().put("outpar", "outpar");
				System.err.println(map().get("inpar"));
			}
		}.set(Convert.toMap("inpar", "inpar", "1", "2", "3", "4")).go().map().get("outpar"));
	}

	@Test
	public void testTrans2() {
		new Trans(false) {
			public void run() {
				System.err.println("trans 4 start");
				accountDao.list(null);
				System.err.println("trans 4 end");
			}
		}.go().go().go().go().go();
	}

	@Test
	public void testTrans() {
		new Trans() {
			public void run() {
				System.err.println("trans 1 start");
				accountDao.list(null);
				new Trans() {
					public void run() {
						System.err.println("trans 2 start");
						accountDao.list(null);
						new Trans() {
							public void run() {
								System.err.println("trans 3 start");
								accountDao.list(null);
								System.err.println("trans 3 end");
							}
						};
						accountDao.list(null);
						System.err.println("trans 2 end");
					}
				};
				accountDao.list(null);
				System.err.println("trans 1 end");
			}
		};
	}
}