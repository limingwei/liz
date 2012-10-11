package com.cduer.people.action;

import li.annotation.At;
import li.annotation.Bean;
import li.mvc.AbstractAction;

@Bean
public class CollegeAction extends AbstractAction {
	@At("college_list.do")
	public void list() {

	}
}