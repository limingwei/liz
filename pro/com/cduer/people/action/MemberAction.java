package com.cduer.people.action;

import com.cduer.people.record.Member;

import li.annotation.Arg;
import li.annotation.At;
import li.annotation.Bean;
import li.annotation.Inject;
import li.mvc.AbstractAction;
import li.util.Convert;
import li.util.Page;

@Bean
public class MemberAction extends AbstractAction {
	@Inject
	Member memberRecord;

	@At("member_list.do")
	public void list(@Arg("pn") Page page) {
		setRequest("members", memberRecord.list(page));
		setRequest("page", page);
		setRequest("nextCode", memberRecord.nextCode());
		view("member_list");
	}

	@At(value = "member_save.do", method = "POST")
	public void save(Member member) {
		memberRecord.save(member);
	}

	@At("member_edit.do")
	public void edit(Integer id) {
		write(Convert.toJson(memberRecord.findById(id)));
	}

	@At(value = "member_update.do", method = "POST")
	public void update(Member member) {
		memberRecord.update(member);
	}

	@At(value = "member_delete.do", method = "POST")
	public void delete(Integer id) {
		memberRecord.deleteById(id);
	}
}