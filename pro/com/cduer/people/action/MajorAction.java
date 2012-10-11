package com.cduer.people.action;

import com.cduer.people.record.Major;

import li.annotation.At;
import li.annotation.Bean;
import li.annotation.Inject;
import li.mvc.AbstractAction;
import li.util.Convert;

@Bean
public class MajorAction extends AbstractAction {
	@Inject
	Major majorRecord;

	@At("major_list.do")
	public void list(Integer collegeId) {
		write(Convert.toJson(majorRecord.listByCollegeId(collegeId)));
	}
}