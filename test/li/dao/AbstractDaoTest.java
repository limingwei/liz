package li.dao;

import java.util.Map;

import li.annotation.Inject;
import li.test.BaseTest;
import li.util.Convert;

import org.junit.Test;

import demo.record.Account;

public class AbstractDaoTest extends BaseTest {
	@Inject
	Account dao;

	@Test
	public void test() {
		Map<?, ?> map = Convert.toMap(":uname", "li", ":eml", "li@w.cn");
		dao.list(page, "WHERE username=:uname OR email=:eml", map);
		// SELECT * FROM t_account WHERE username='li' OR email='li@w.cn'

		dao.find("WHERE username=? OR email=?", "li", "li@w.cn");
		// SELECT * FROM t_account WHERE username='li' OR email='li@w.cn'

		dao.delete("WHERE username=:uname OR email=:eml OR id=?", map, 1);
		// DELETE FROM t_account WHERE username='li' OR email='li@w.cn' OR id='1'
	}
}
