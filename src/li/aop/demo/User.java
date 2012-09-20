package li.aop.demo;

public class User {
	private int age;

	public User() {
		System.out.println("User 的默认构造函数");
	}

	public User(int age) {
		this.age = age;
		System.out.println("代一个参数的构造函数");
	}

	public void sayHi() {
		System.out.println(this + " userr say hi 1111111111");
	}

	public void sayHi2() {
		System.out.println(this + " userr say hi 222222222");
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
