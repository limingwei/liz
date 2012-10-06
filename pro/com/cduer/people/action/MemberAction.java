package com.cduer.people.action;

import com.cduer.people.record.Member;

import li.annotation.Arg;
import li.annotation.At;
import li.annotation.Bean;
import li.annotation.Inject;
import li.mvc.AbstractAction;
import li.util.Page;

@Bean
public class MemberAction extends AbstractAction {
	@Inject
	Member memberDao;

	@At("member_list")
	public void list(@Arg("pn") Page page) {
		setRequest("members", memberDao.list(page));
		setRequest("page", page);
		view("member_list");
	}

	@At("member_add")
	public void add() {
		view("member_add");
	}

	@At(value = "member_save", method = "POST")
	public void save(Member member) {
		memberDao.save(member);
	}
}