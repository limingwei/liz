package demo.action;

import static org.junit.Assert.assertNotNull;
import li.annotation.Inject;
import li.test.BaseActionTest;

import org.junit.Before;
import org.junit.Test;

public class MemberActionTest extends BaseActionTest {
	@Inject
	MemberAction memberAction;

	@Before
	public void before() {
		assertNotNull(memberAction);
	}

	@Test
	public void find() {
		// memberAction.find(1);
	}

	@Test
	public void test1() {
	}

	@Test
	public void testFreemarker() {
	}

	@Test
	public void testVelocity() {
	}

}