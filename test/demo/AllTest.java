package demo;

import li.FrameworkTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FrameworkTest.class, AllActionTest.class, AllRecordTest.class, AllServiceTest.class })
public class AllTest {
}