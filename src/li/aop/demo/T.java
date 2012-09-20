package li.aop.demo;

import li.aop.AopClassLoader;

public class T {
	public static void main(String[] args) throws Exception {
		User user = AopClassLoader.newInstance(User.class);
		user.sayHi();
		user.sayHi();
		user.sayHi();

		System.out.println(user.toString());

		user.getClass().getMethod("newMethod").invoke(user);
	}
}