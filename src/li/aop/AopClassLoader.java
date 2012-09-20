package li.aop;

import li.aop.demo.User;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class AopClassLoader extends ClassLoader implements Opcodes {
	/**
	 * ##
	 */
	private static AopClassLoader instance = new AopClassLoader();

	/**
	 * ##
	 */
	public static <T> T born(Class<T> type) {
		try {
			return instance.handle(type).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ##
	 */
	private Class<?> defineClass(String type, byte[] bytes) throws ClassFormatError {
		return super.defineClass(type + "$Aop", bytes, 0, bytes.length);
	}

	/**
	 * ##
	 */
	private <T> Class<T> handle(Class<T> type) {
		try {
			ClassReader classReader = new ClassReader(type.getName());
			ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
			ClassVisitor classVisitor = new AopClassVisitor(classWriter);
			classReader.accept(classVisitor, ASM4);
			byte[] bytes = classWriter.toByteArray();
			return (Class<T>) instance.defineClass(User.class.getName(), bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
