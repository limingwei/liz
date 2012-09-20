package li.aop.demo;

import li.aop.AopClassLoader;
import li.aop.AopClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class T implements Opcodes {
	public static void main(String[] args) throws Exception {
		ClassReader classReader = new ClassReader(User.class.getName());
		ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
		ClassVisitor classVisitor = new AopClassVisitor(classWriter);
		classReader.accept(classVisitor, ASM4);
		byte[] bytes = classWriter.toByteArray();

		Class<?> type = new AopClassLoader().defineClass(User.class.getName(), bytes);

		User user = (User) type.newInstance();

		user.sayHi();
	}
}