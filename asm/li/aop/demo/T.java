package li.aop.demo;

import li.aop.AopClassLoader;

import org.objectweb.asm.Opcodes;

public class T implements Opcodes {
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception {
		User user = AopClassLoader.newInstance(User.class);
		user.sayHi();
		
		user.sayHi("haha");
	}
}