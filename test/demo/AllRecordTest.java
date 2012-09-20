package demo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import demo.record.AccountTest;
import demo.record.ForumTest;
import demo.record.MemberTest;
import demo.record.PostTest;
import demo.record.ThreadTest;

@RunWith(Suite.class)
@SuiteClasses({ AccountTest.class, ForumTest.class, MemberTest.class, PostTest.class, ThreadTest.class })
public class AllRecordTest {
}