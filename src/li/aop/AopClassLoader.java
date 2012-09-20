package li.aop;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class AopClassLoader extends ClassLoader implements Opcodes {
	/**
	 * AopClassLoader的单例的实例
	 */
	private static AopClassLoader instance = new AopClassLoader();

	/**
	 * 返回Aop处理后的类的实例
	 */
	public static <T> T newInstance(Class<T> type) {
		try {
			return instance.handle(type).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Aop处理类后返回
	 */
	private <T> Class<T> handle(Class<T> type) {
		try {
			ClassReader classReader = new ClassReader(type.getName());
			ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

			ClassVisitor classVisitor = new AopClassVisitor(classWriter);
			classReader.accept(classVisitor, ASM4);

			byte[] bytes = classWriter.toByteArray();
			return (Class<T>) instance.defineClass(type.getName() + "$Aop", bytes, 0, bytes.length);// byte 数组转换为 Class 类的实例
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
