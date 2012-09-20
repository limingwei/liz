package li.aop;

import java.io.File;

import li.util.Files;

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
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

			ClassVisitor classVisitor = new AopClassVisitor(classWriter);// 使用AopClassVisitor加载并修改Class
			classReader.accept(classVisitor, ASM4);

			byte[] bytes = classWriter.toByteArray();

			Files.write(type, bytes);// 把修改后的Class写到桌面，供查看

			return (Class<T>) instance.defineClass(type.getName() + "$Aop", bytes, 0, bytes.length);// byte 数组转换为Class 类
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
