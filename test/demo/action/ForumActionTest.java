package demo.action;

import static org.junit.Assert.assertNotNull;
import li.annotation.Inject;
import li.test.BaseTest;

import org.junit.Before;
import org.junit.Test;

public class ForumActionTest extends BaseTest {
	@Inject
	ForumAction forumAction;

	@Before
	public void before() {
		assertNotNull(forumAction);
	}

	@Test
	public void list() {
	}
}