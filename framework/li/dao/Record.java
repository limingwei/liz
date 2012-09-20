package li.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import li.model.Field;
import li.util.Page;

/**
 * 你的数据对象类可以继承这个类,以使用ActiveRecord的方式操作数据库
 * 
 * @author li (limw@w.cn)
 * @version 0.1.1 (2012-06-25)
 */
public class Record<T extends Record> extends AbstractDao<T> implements Map<String, Object>, Serializable {
	/**
	 * 存储对象属性值的Map
	 */
	private Map<String, Object> fields = new HashMap<String, Object>();

	/**
	 * 重写AbstractDao中的list方法,使Record的find和 list方法 支持多表查询
	 */
	public List<T> list(Page page, String sql, Object... args) {
		sql = getQueryBuilder().list(page, sql, args);

		QueryRunner queryRunner = new QueryRunner(getConnection());
		ResultSet resultSet = queryRunner.executeQuery(sql);
		ModelBuilder modelBuilder = new ModelBuilder(queryRunner, resultSet);
		List<Field> fields = Field.list(resultSet);

		if (null != resultSet && null != page) {
			page.setRecordCount(count(sql));
		}

		return modelBuilder.list(getType(), fields, (null == page ? 18 : page.getPageSize()), true);
	}

	/**
	 * 得到名为key的属性
	 */
	public Object get(Object key) {
		return fields.get(key);
	}

	/**
	 * 设置名为key的属性的值为value
	 */
	public Object put(String key, Object value) {
		return fields.put(key, value);
	}

	/**
	 * 设置名为key的属性的值为value
	 */
	public T set(String key, Object value) {
		fields.put(key, value);
		return (T) this;
	}

	/**
	 * 返回Record的属性个数
	 */
	public int size() {
		return fields.size();
	}

	/**
	 * 是否为空,即是否属性个数为0
	 */
	public boolean isEmpty() {
		return fields.isEmpty();
	}

	/**
	 * 是否包含名为key的属性
	 */
	public boolean containsKey(Object key) {
		return fields.containsKey(key);
	}

	/**
	 * 是否包含值为value的属性
	 */
	public boolean containsValue(Object value) {
		return fields.containsValue(value);
	}

	/**
	 * 移除名为key的属性
	 */
	public Object remove(Object key) {
		return fields.remove(key);
	}

	/**
	 * 批量设置属性值
	 */
	public void putAll(Map<? extends String, ? extends Object> map) {
		fields.putAll(map);
	}

	/**
	 * 清空所有属性
	 */
	public void clear() {
		fields.clear();
	}

	/**
	 * 返回属性的Set集合
	 */
	public Set<String> keySet() {
		return fields.keySet();
	}

	/**
	 * 返回属性值得Collection集合
	 */
	public Collection<Object> values() {
		return fields.values();
	}

	/**
	 * 返回属性值的Set集合
	 */
	public Set<Entry<String, Object>> entrySet() {
		return fields.entrySet();
	}
}