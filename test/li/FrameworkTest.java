package li;

import li.aop.AopTest;
import li.dao.AbstractDaoTest;
import li.dao.QueryBuilderTest;
import li.dao.QueryRunnerTest;
import li.dao.RecordTest;
import li.dao.TransTest;
import li.ioc.IocTest;
import li.model.BeanTest;
import li.model.FieldTest;
import li.mvc.ContextTest;
import li.service.AbstractServiceTest;
import li.util.ConvertTest;
import li.util.PageTest;
import li.util.ReflectTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AopTest.class, AbstractDaoTest.class, QueryRunnerTest.class, PageTest.class, RecordTest.class, QueryBuilderTest.class, TransTest.class, IocTest.class, BeanTest.class, FieldTest.class, ContextTest.class, AbstractServiceTest.class, ConvertTest.class, ReflectTest.class })
public class FrameworkTest {

}