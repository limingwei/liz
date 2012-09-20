package demo.action;

import static org.junit.Assert.assertNotNull;
import li.annotation.Inject;
import li.test.BaseActionTest;

import org.junit.Before;
import org.junit.Test;

public class AccountActionTest extends BaseActionTest {
	@Inject
	AccountAction accountAction;

	@Before
	public void before() {
		assertNotNull(accountAction);
	}

	@Test
	public void listTest() {
	}
}