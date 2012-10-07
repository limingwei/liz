package com.cduer.people.record;

import li.annotation.Bean;
import li.annotation.Table;
import li.dao.Record;

@Bean
@Table("t_member")
public class Member extends Record<Member> {
	public String nextCode() {
		String lastCode = (String) find("ORDER BY code DESC").get("code");
		return (new Long(lastCode) + 1L) + "";
	}
}