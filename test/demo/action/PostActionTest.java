package demo.action;

import static org.junit.Assert.assertNotNull;
import li.annotation.Inject;
import li.test.BaseActionTest;

import org.junit.Before;
import org.junit.Test;

public class PostActionTest extends BaseActionTest {
	@Inject
	PostAction postAction;

	@Before
	public void before() {
		assertNotNull(postAction);
	}

	@Test
	public void testList() {
	}
}