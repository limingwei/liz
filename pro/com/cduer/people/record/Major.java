package com.cduer.people.record;

import java.util.List;

import li.annotation.Bean;
import li.annotation.Table;
import li.dao.Record;
import li.util.Page;

@Bean
@Table("t_major")
public class Major extends Record<Major> {
	public List<Major> listByCollegeId(Integer collegeId) {
		return list(new Page(1, 100), "WHERE college_Id=?", collegeId);
	}
}