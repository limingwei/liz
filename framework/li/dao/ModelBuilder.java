package li.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import li.model.Field;
import li.util.Log;
import li.util.Reflect;

/**
 * Dao辅助类,从ResultSet中读取数据并组装对象
 * 
 * @author li (limw@w.cn)
 * @version 0.1.5 (2012-05-08)
 * @see li.util.Reflect
 */
public class ModelBuilder {
	private static final Log log = Log.init();

	/**
	 * 保存当前查询的QueryRunner,为了回调以关闭Connection
	 */
	private QueryRunner queryRunner;

	/**
	 * 当前ModelBuilder的结果集
	 */
	private ResultSet resultSet;

	/**
	 * 初始化一个ModelBuilder
	 */
	public ModelBuilder(QueryRunner queryRunner, ResultSet resultSet) {
		this.queryRunner = queryRunner;
		this.resultSet = resultSet;
	}

	/**
	 * 从ResultSet中提取数据组装成type类型的对象的 List
	 * 
	 * @param type 对象类型
	 * @param fields 对象属性列表
	 * @param count 组装对象最大个数
	 * @param close 是否于读取后关闭resultSet
	 */
	public <T> List<T> list(Class<T> type, List<Field> fields, Integer count, Boolean close) {
		List<T> list = new ArrayList<T>();
		try {
			while (null != resultSet && resultSet.next() && (null == count || list.size() <= count)) {
				T t = Reflect.born(type);
				for (Field attribute : fields) {
					Reflect.set(t, attribute.name, value(attribute.column, false, false));
				}
				list.add(t);
			}
			log.info(String.format("builded %s models, types of %s", list.size(), type.getName()));
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.dao.ModelBuilder.list()", e);
		} finally {
			if (close) {
				this.close();
			}
		}
		return list;
	}

	/**
	 * 辅助方法,封装后的resultSet.getString(column);
	 * 
	 * @param column 数据所在的列名
	 * @param next 读取前是否 resultSet.next()
	 * @param close 是否于读取后关闭resultSet
	 */
	public String value(String column, Boolean next, Boolean close) {
		try {
			if (null != resultSet && next) { // 若next==true,则resultSet.next()
				if (resultSet.next()) { // 若resultSet.next()返回true,则从resultSet中读值
					return resultSet.getString(column);
				}
			} else if (null != resultSet && !next) { // 若next==false,则直接从resultSet中取值
				return resultSet.getString(column);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.dao.ModelBuilder.value()", e);
		} finally {
			if (close) {
				this.close();
			}
		}
		return null;
	}

	/**
	 * 关闭ModelBuilder,关闭ResultSet,PreparedStatement和Connection
	 * 
	 * @see li.dao.QueryRunner#close()
	 */
	private void close() {
		try {
			if (null != resultSet) {
				resultSet.close();// 关闭ResultSet
			}
			if (null != queryRunner) {
				queryRunner.close();// 关闭QueryRunner,他会关闭PreparedStatement和Connection
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception at li.dao.ModelBuilder.close()", e);
		}
	}
}