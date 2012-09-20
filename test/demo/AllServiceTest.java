package demo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import demo.service.UserServiceTest;

@RunWith(Suite.class)
@SuiteClasses({ UserServiceTest.class })
public class AllServiceTest {

}
