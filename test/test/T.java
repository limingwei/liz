package test;

import li.ioc.Ioc;

public class T {
	public static void main(String[] args) {
		User user = Ioc.get(User.class);
		Account account = Ioc.get(Account.class);
		System.out.println(user);

		account.list(null);
	}
}