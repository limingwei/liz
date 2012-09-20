package li;

import li.model.BeanTest;
import li.model.FieldTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BeanTest.class, FieldTest.class })
public class AllModelTest {

}