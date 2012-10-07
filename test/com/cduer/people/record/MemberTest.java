package com.cduer.people.record;

import org.junit.Test;

import li.annotation.Inject;
import li.test.BaseTest;

public class MemberTest extends BaseTest {
	@Inject
	Member memberRecord;

	@Test
	public void nextCode() {
		System.out.println(memberRecord.nextCode());
	}
}