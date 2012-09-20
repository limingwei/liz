package li.aop.demo;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class T implements Opcodes {
	public static void main(String[] args) throws Exception {
//		User user = AopClassLoader.newInstance(User.class);
//		user.sayHi();
//
//		user.setAge(123);
//		System.out.println(user.getAge());

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(User.class.getName());
		// 注意classReader.accept的位置，必须为在填充了Node信息之前，如之后就得不到本来类中的信息。
		classReader.accept(classNode, 0);
		
		System.out.println(classNode.invisibleAnnotations);
	}

	public static void main2(String[] args) {

	}
}