package li.aop.demo;

public class User {
	public User() {
		System.out.println("User 的构造函数");
	}

	public void sayHi() {
		System.out.println(this + " user say hi");
	}

	public void sayHello() {
		System.out.println(this + " Saying Hello");
	}
}
