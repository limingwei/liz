package li.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import li.dao.QueryRunner;
import li.util.Log;
import li.util.Verify;

/**
 * 属性对象,表示一个Bean的一个属性,或者Table的一个列
 * 
 * @author li (limw@w.cn)
 * @version 0.1.7 (2012-05-08)
 */
public class Field {
	private static final Log log = Log.init();

	/**
	 * Map,用于缓存对象的属性列表
	 */
	private static final Map<String, List<Field>> FIELDS_MAP = new HashMap<String, List<Field>>();

	/**
	 * 属性名
	 */
	public String name;

	/**
	 * 属性的JAVA类型
	 */
	public Class<?> type;

	/**
	 * 这个属性的值
	 */
	public String value;

	/**
	 * 属性对应数据库表的列名
	 */
	public String column;

	/**
	 * 这个属性是否一个ID
	 */
	public Boolean isId;

	/**
	 * 通过扫描对象结构及注解的方式得到一个类型的属性列表 List<Field>,根据类名缓存
	 * 
	 * @param targetType 目标对象
	 * @param annotated 是否只列出有Field注解的字段
	 */
	public static List<Field> list(Class<?> targetType, Boolean annotated) {
		List<Field> fields = FIELDS_MAP.get("class#" + targetType.getName() + "#annotated#" + annotated);
		if (null == fields) { // 如果缓存中没有
			log.info(String.format("Field.list() by type %s", targetType.getName()));
			fields = new ArrayList<Field>();
			for (java.lang.reflect.Field field : targetType.getDeclaredFields()) {
				li.annotation.Field column = field.getAnnotation(li.annotation.Field.class);
				if (!annotated || null != column) {// 如果不需要Field注解或者Field注解不为空
					li.model.Field attribute = new li.model.Field();
					attribute.name = field.getName();
					attribute.column = (null == column || Verify.isEmpty(column.value())) ? field.getName() : column.value();
					attribute.isId = (null == column) ? false : column.id();
					fields.add(attribute);
				}
			}
			// 扫描这个类的超类中的Field加入到当前类的List<Field>中,使@Field注解支持继承
			if (Object.class != targetType.getSuperclass()) {
				fields.addAll(list(targetType.getSuperclass(), annotated));
			}
			FIELDS_MAP.put("class#" + targetType.getName() + "#annotated#" + annotated, fields); // 加入缓存
		}
		return fields;
	}

	/**
	 * 通过 DESC tableName的方式得到一个类型(表)的属性(字段)列表 List<Field>,根据表名缓存
	 * 
	 * @param dataSource 通过desc tableName解析表结构时候需要用到的数据源
	 * @param table 需要探测表结构的数据表名称
	 */
	public static List<Field> list(DataSource dataSource, String table) {
		List<Field> fields = FIELDS_MAP.get("table#" + table);
		if (null == fields && null != dataSource) { // 如果缓存中没有
			log.info(String.format("Field.list() by table %s", table));
			try {
				fields = new ArrayList<Field>();
				String sql = String.format("DESC %s", table);

				QueryRunner queryRunner = new QueryRunner(dataSource.getConnection());
				ResultSet resultSet = queryRunner.executeQuery(sql);

				while (null != resultSet && resultSet.next()) {
					Field attribute = new Field();
					attribute.name = attribute.column = resultSet.getString("Field");
					attribute.isId = resultSet.getString("Key").toUpperCase().equals("PRI");// Key的值为PRI的字段即为ID字段
					fields.add(attribute);
				}
				resultSet.close();// 关闭resultSet
				queryRunner.close();// 关闭QueryRunner
				FIELDS_MAP.put("table#" + table, fields); // 加入缓存
			} catch (Exception e) {
				throw new RuntimeException("Exception in li.model.Field.list(DataSource, String)", e);
			}
		}
		return fields;
	}

	/**
	 * 通过 resultSet.getMetaData()的方式得到一个结果集的列的列表
	 */
	public static List<Field> list(ResultSet resultSet) {
		List<Field> fields = new ArrayList<Field>();
		try {
			ResultSetMetaData meta = (null == resultSet ? null : resultSet.getMetaData());
			for (int i = 1; (null != meta && i <= meta.getColumnCount()); i++) {
				Field attribute = new Field();
				attribute.name = attribute.column = meta.getColumnLabel(i);
				fields.add(attribute);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception in li.model.Field.list(ResultSet)", e);
		}
		return fields;
	}
}
