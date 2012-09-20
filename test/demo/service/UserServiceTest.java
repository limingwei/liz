package demo.service;

import li.annotation.Inject;
import li.test.BaseTest;

import org.junit.Test;

import demo.model.User;

public class UserServiceTest extends BaseTest {
	@Inject
	IUserService userService;

	@Test
	public void testLogin() {
		User user = new User();
		user.setUsername("uname");
		user.setPassword("pwd");
		user.setEmail("eml");

		System.out.println(userService.login(user));
	}
}