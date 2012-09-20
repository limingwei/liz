package li.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import li.dao.Record;

import org.junit.Test;

public class ConvertTest {
	@Test
	public void toType() {
		String str2 = Convert.toType(String.class, "str");
		assertEquals("str", str2);

		java.util.Date date1 = Convert.toType(java.util.Date.class, "2012-02-11");
		java.sql.Date date2 = Convert.toType(java.sql.Date.class, "2012-02-11");
		java.sql.Time time = Convert.toType(java.sql.Time.class, "17:30:00");
		java.sql.Timestamp timestamp = Convert.toType(java.sql.Timestamp.class, "2012-02-11 17:30:00");

		System.err.println(timestamp + "\t" + date2 + "\t" + time + "\t" + date1);
	}

	@Test
	public void toJson() {
		List<Record<?>> list = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Record<?> record = new Record<>();
			record.set("id", 1);
			record.set("username", "li");
			record.set("password", "wode");
			record.set("email", "limw@w.cn");
			list.add(record);
		}
		String json = Convert.toJson(list);
		System.out.println(json);
	}

	@Test
	public void fromJson() {
		List<Record<?>> list = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Record<?> record = new Record<>();
			record.set("id", 1);
			record.set("username", "li");
			record.set("password", "wode");
			record.set("email", "limw@w.cn");
			list.add(record);

			System.out.println(Convert.fromJson(Record.class, Convert.toJson(record)));
		}

		List<List<Record<?>>> list2 = new ArrayList<>();
		list2.add(list);
		list2.add(list);
		list2.add(list);

		String json = Convert.toJson(list);

		List<Record> records = Convert.fromJson(Record.class, json);

		System.out.println(records);

		String json2 = Convert.toJson(Convert.fromJson(Record.class, Convert.toJson(Convert.fromJson(Record.class, Convert.toJson(list)))));

		System.out.println(json2);

		assertEquals(json, json2);
	}

	@Test
	public void toMap() {

	}
}