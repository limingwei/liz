package li.dao;

import java.util.Map;

import javax.sql.DataSource;

import li.ioc.Ioc;
import li.model.Bean;
import li.test.BaseTest;
import li.util.Convert;
import li.util.Page;

import org.junit.Before;
import org.junit.Test;

import demo.record.Account;

public class QueryBuilderTest extends BaseTest {
	private QueryBuilder QUERY_BUILDER;

	@Before
	public void before() throws Exception {
		DataSource dataSource = Ioc.get(DataSource.class);

		QUERY_BUILDER = new QueryBuilder(dataSource, Bean.getMeta(dataSource, Account.class));
	}

	@Test
	public void testSetArgs() {
		String sql = "SELECT * FROM t_account where username=?";
		Object[] args = { "uuu" };

		sql = QUERY_BUILDER.setArgs(sql, args);

		System.out.println(sql);
	}

	@Test
	public void testSetArgs2() {
		String sql = "SELECT * FROM t_account where username=#username";
		Map<Object, Object> args = Convert.toMap("#username", "uuu");
		sql = QUERY_BUILDER.setArgs(sql, args);

		System.out.println(sql);
	}

	@Test
	public void testSetAlias() {
		String sql = "SELECT t_account.#,t_forum.# as f_#,t_member.#,t_post.# AS p_# FROM t_account";
		sql = QUERY_BUILDER.setAlias(sql);
		System.err.println(sql);
	}

	@Test
	public void testSetPage() {
		String sql = "SELECT * FROM t_account";
		sql = QUERY_BUILDER.setPage(sql, new Page(1, 1));
		System.err.println(sql);
	}
}
