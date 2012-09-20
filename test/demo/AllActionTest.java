package demo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import demo.action.AccountActionTest;
import demo.action.ForumActionTest;
import demo.action.MemberActionTest;
import demo.action.PostActionTest;
import demo.action.ThreadActionTest;

@RunWith(Suite.class)
@SuiteClasses({ AccountActionTest.class, ForumActionTest.class, MemberActionTest.class, PostActionTest.class, ThreadActionTest.class })
public class AllActionTest {
}