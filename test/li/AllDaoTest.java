package li;

import li.dao.AbstractDaoTest;
import li.dao.QueryBuilderTest;
import li.dao.QueryRunnerTest;
import li.dao.RecordTest;
import li.dao.TransTest;
import li.util.PageTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractDaoTest.class, QueryRunnerTest.class, PageTest.class, RecordTest.class, QueryBuilderTest.class, TransTest.class })
public class AllDaoTest {

}