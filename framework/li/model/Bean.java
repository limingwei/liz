package li.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import li.annotation.Table;
import li.dao.Record;
import li.util.Log;
import li.util.Verify;

/**
 * Bean对象,表示一个IocBean或TableBean
 * 
 * @author li (limw@w.cn)
 * @version 0.1.6 (2012-05-08)
 */
public class Bean {
	private static final Log log = Log.init();

	/**
	 * 缓存Bean结构的Map
	 */
	private static final Map<Class<?>, Bean> BEAN_MAP = new HashMap<Class<?>, Bean>();

	/**
	 * Bean的一个实例
	 */
	public Object instance;

	/**
	 * Bean的名称
	 */
	public String name;

	/**
	 * Bean的类型
	 */
	public Class<?> type;

	/**
	 * Bean的属性集合
	 */
	public List<Field> fields = new ArrayList<Field>();

	/**
	 * TableBean的数据库表名
	 */
	public String table;

	/**
	 * 这个Bean的Id,从getId()方法得到
	 */
	private Field id;

	/**
	 * 得到这个对象的ID,Field类型,有缓存的
	 */
	public Field getId() {
		if (null == this.id) {
			for (Field field : fields) {
				if (field.isId) {
					this.id = field;
					break;// 跳出for循环
				}
			}
			if (null == this.id) {
				throw new RuntimeException(String.format("As a POJO,%s must has a primary key field which types of Integer and annotationed by @Field(id=true)", type));
			}
		}
		return this.id;
	}

	/**
	 * 根据注解等方式得到一个类型的结构,其中包含属性列表和tableName等,根据类名缓存
	 * 
	 * @param dataSource 通过DESC tableName解析表结构时候需要用到的数据源
	 * @param type 目标类型
	 */
	public static Bean getMeta(DataSource dataSource, Class<?> type) {
		Bean bean = BEAN_MAP.get(type);
		if (null == bean) {
			log.info(String.format("Bean.getMeta(%s) ", type.getName()));
			bean = new Bean();
			Table table = type.getAnnotation(Table.class);
			bean.table = (null == table || Verify.isEmpty(table.value())) ? type.getSimpleName() : table.value();
			bean.fields = Record.class.isAssignableFrom(type) ? Field.list(dataSource, bean.table) : Field.list(type, true);// 若type为Record的子类型,则用DESC方式,否则用扫描对象方式
			BEAN_MAP.put(type, bean);
		}
		return bean;
	}
}