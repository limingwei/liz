package li.dao;

import java.util.List;

import li.annotation.Inject;
import li.test.BaseTest;

import org.junit.Test;

public class RecordTest extends BaseTest {
	@Inject
	User userDao;

	@Inject
	UserDao userDao2;

	User user = new User().set("id", 1).set("username", "u-4-1" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1");

	@Test
	public void test1() {
		new Trans() {
			public void run() {
				System.out.println("啥都没做");
			}
		};
	}

	@Test
	public void test2() {
		new Trans() {
			public void run() {
				System.out.println("啥都没做");
			}
		};
	}

	@Test
	public void testRecord() {
		// List<User> records = userDao.list(page,
		// "select t_account.username as uname,t_forum.name as fname from t_account,t_forum");
		// System.out.println(records);
	}

	@Test
	public void count() {
		// assertNotNull(userDao.count());
		System.out.println("li.dao.test.ModelTest.count()" + userDao.count());
	}

	@Test
	public void count2() {
		System.err.println("li.dao.test.ModelTest.count2()\n" + userDao.count("where false"));
	}

	@Test
	public void delete() {
		// assertFalse(userDao.delete(-2));
	}

	@Test
	public void delete2() {
		// assertNotNull(userDao.delete("where false"));
		System.err.println("li.dao.test.ModelTest.delete2()\n" + userDao.delete("where false"));
	}

	@Test
	public void find() {
		// assertNotNull(userDao.find(1));
		System.out.println("li.dao.test.ModelTest.find()" + userDao.find(1));
	}

	@Test
	public void find2() {
		System.out.println("li.dao.test.ModelTest.find2()" + userDao.find("where true limit 1"));
	}

	@Test
	public void find3() {
		System.err.println("li.dao.test.ModelTest.find3()");
		userDao = userDao.find("select t_account.username as uname,t_forum.name as fname from t_account,t_forum limit 1");
		System.out.println(userDao);
	}

	@Test
	public void list() {
		System.err.println("li.dao.test.ModelTest.list()");
		// assertNotNull(userDao.list(page));
		System.out.println(userDao.list(page));
	}

	@Test
	public void list2() {
		System.err.println("li.dao.test.ModelTest.list2()");
		// assertNotNull(userDao.list(page, "where true"));
		// System.out.println(userDao.list(page, "where true"));
	}

	@Test
	public void list3() {
		System.err.println("li.dao.test.ModelTest.list3()");
		List<User> users = userDao.list(page.setPageSize(5), "select t_account.username as uname,t_forum.name as fname from t_account,t_forum");
		System.out.println(users);
	}

	@Test
	public void save() {
		userDao.save(new User().set("username", "u-2" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1").set("status", 1));
	}

	@Test
	public void save2() {
		userDao.save(new User().set("id", null).set("username", "u-3" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1").set("status", 1));
	}

	@Test
	public void update() {
		userDao.update(userDao.set("id", 1).set("username", "u-4" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1").set("status", 1));
	}

	@Test
	public void update2() {
		userDao.update(userDao.set("id", 2).set("username", "u-5" + System.currentTimeMillis()).set("password", "p-1").set("email", "e-1").set("status", 1));
	}
}