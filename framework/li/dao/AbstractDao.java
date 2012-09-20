package li.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import li.ioc.Ioc;
import li.model.Bean;
import li.util.Log;
import li.util.Page;
import li.util.Reflect;

/**
 * AbstractDao,通常,业务对象的Dao要继承这个基类
 * 
 * @author li (limw@w.cn)
 * @version 0.1.7 (2012-06-26)
 */
public class AbstractDao<T> implements IBaseDao<T> {
	private static final Log log = Log.init();

	/**
	 * 泛型参数的实际类型,即是数据对象类型
	 */
	private Class<T> modelType;

	/**
	 * 存储数据对象结构
	 */
	private Bean beanMeta;

	/**
	 * QueryBuilder,你可以通过Ioc配置
	 */
	private QueryBuilder queryBuilder;

	/**
	 * 当前Dao的DataSource,可以通过Ioc配置
	 */
	private DataSource dataSource;

	/**
	 * 通过泛型参数得到modelType
	 */
	protected Class<T> getType() {
		if (null == this.modelType) {
			this.modelType = (Class<T>) Reflect.actualType(getClass(), 0); // 通过泛型参数得到modelType
		}
		return this.modelType;
	}

	/**
	 * 得到当前Dao所操作的数据对象的结构
	 */
	protected Bean getBeanMeta() {
		if (null == this.beanMeta) {
			this.beanMeta = Bean.getMeta(getDataSource(), getType());
		}
		return this.beanMeta;
	}

	/**
	 * 得到SQL构造器,可以覆盖这个方法来配置QueryBuilder
	 */
	protected QueryBuilder getQueryBuilder() {
		if (null == this.queryBuilder) {
			this.queryBuilder = new QueryBuilder(getDataSource(), getBeanMeta());
		}
		return this.queryBuilder;
	}

	/**
	 * 如果还没有注入dataSource,则尝试次从Ioc中搜索DataSource类型的Bean, 可覆盖这个方法来配置DataSource
	 */
	protected DataSource getDataSource() {
		if (null == this.dataSource) {
			this.dataSource = Ioc.get(DataSource.class);
			log.warn(String.format("DataSource not injected for %s,Tring to search one more time", this));
		}
		return this.dataSource;
	}

	/**
	 * 得到一个Connection,从Trans或者从Datasource
	 * 
	 * @see li.dao.Trans
	 * @see li.dao.QueryRunner
	 */
	protected Connection getConnection() {
		try {
			if (null == Trans.CONNECTION_MAP.get()) {// 如果未进入事务
				return this.getDataSource().getConnection();// 则简单获取一个connection
			} else { // 如果已经进入事务
				Connection connection = Trans.CONNECTION_MAP.get().get(getClass()); // 从connectionMap中得到缓存的connection
				if (null == connection || connection.isClosed()) { // 没有缓存这个Dao的connection或已被关闭
					connection = this.getDataSource().getConnection(); // 获取一个新的connection
					connection.setAutoCommit(false); // 设置为不自动提交
					Trans.CONNECTION_MAP.get().put(getClass(), connection); // 缓存connection
				}
				return connection; // 返回这个connection
			}
		} catch (Exception e) {
			throw new RuntimeException("error to connect to the database", e);
		}
	}

	/**
	 * 查询对象对应的表的总记录数
	 */
	public Integer count() {
		return count(getQueryBuilder().count());
	}

	/**
	 * 根据SQL条件查询记录数,SQL语句可以从WHERE开始写,当然也可以是完整的SQL
	 * 
	 * @param sql 传入的sql语句,可以包含'?'占位符和具名占位符
	 * @param args 替换sql中占位符的值,或者对应具名占位符的Map
	 */
	public Integer count(String sql, Object... args) {
		sql = getQueryBuilder().count(sql, args);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		ResultSet resultSet = queryRunner.executeQuery(sql);
		ModelBuilder modelBuilder = new ModelBuilder(queryRunner, resultSet);

		return new Integer(modelBuilder.value("COUNT(*)", true, true));
	}

	/**
	 * 根据ID查询一条记录
	 */
	public T find(Integer id) {
		return find(getQueryBuilder().find(id));
	}

	/**
	 * 根据SQL条件查询一条记录,SQL语句可以从WHERE开始写,当然也可以是完整的SQL
	 * 
	 * @param sql 传入的sql语句,可以包含'?'占位符和具名占位符
	 * @param args 替换sql中占位符的值,或者对应具名占位符的Map
	 */
	public T find(String sql, Object... args) {
		List<T> list = list(new Page(1, 1), sql, args);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * 根据分页对象进行分页查询,如果page不为NULL,他会被自动设置总记录数
	 */
	public List<T> list(Page page) {
		return list(page, getQueryBuilder().list(page));
	}

	/**
	 * 根据SQL条件和分页对象进行分页查询,SQL语句可以从WHERE开始写,当然也可以是完整的SQL
	 * 
	 * @param sql 传入的sql语句,可以包含'?'占位符和具名占位符
	 * @param args 替换sql中占位符的值,或者对应具名占位符的Map
	 */
	public List<T> list(Page page, String sql, Object... args) {
		sql = getQueryBuilder().list(page, sql, args);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		ResultSet resultSet = queryRunner.executeQuery(sql);
		ModelBuilder modelBuilder = new ModelBuilder(queryRunner, resultSet);

		if (null != resultSet && null != page) {
			page.setRecordCount(count(sql));
		}
		return modelBuilder.list(getType(), getBeanMeta().fields, (null == page ? 18 : page.getPageSize()), true);
	}

	/**
	 * 向数据库中插入一条记录,save方法完成后,对象的ID将会被设值
	 */
	public Boolean save(T t) {
		String sql = getQueryBuilder().save(t);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		Integer updateCount = queryRunner.executeUpdate(sql);

		Reflect.set(t, getBeanMeta().getId().name, queryRunner.LAST_INSERT_ID);// 设置对象ID为最后主键值

		return 1 == updateCount;
	}

	/**
	 * 执行更新类的自定义SQL
	 * 
	 * @param sql 传入的sql语句,可以包含'?'占位符和具名占位符
	 * @param args 替换sql中占位符的值,或者对应具名占位符的Map
	 * @return 受影响的行数
	 */
	public Integer update(String sql, Object... args) {
		sql = getQueryBuilder().update(sql, args);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		return queryRunner.executeUpdate(sql);
	}

	/**
	 * 更新一个对象,根据ID得到对象,然后更新其他属性值
	 */
	public Boolean update(T t) {
		String sql = getQueryBuilder().update(t);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		return 1 == queryRunner.executeUpdate(sql);
	}

	/**
	 * 删除ID等于传入参数的一条记录,如果存在的话
	 */
	public Boolean delete(Integer id) {
		String sql = getQueryBuilder().delete(id);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		return 1 == queryRunner.executeUpdate(sql);
	}

	/**
	 * 根据SQL条件删除若干条数据,SQL语句可以从WHERE开始写,当然也可以是完整的SQL
	 * 
	 * @param sql 传入的sql语句,可以包含'?'占位符和具名占位符
	 * @param args 替换sql中占位符的值,或者对应具名占位符的Map
	 * @return 受影响的行数
	 */
	public Integer delete(String sql, Object... args) {
		sql = getQueryBuilder().delete(sql, args);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		return queryRunner.executeUpdate(sql);
	}
}