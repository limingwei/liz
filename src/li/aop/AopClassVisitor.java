package li.aop;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AopClassVisitor extends ClassVisitor implements Opcodes {
	public AopClassVisitor(ClassWriter classWriter) {
		super(ASM4, classWriter);
	}

	/**
	 * 访问此类时转向访问新构造的子类
	 */
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name + "$Aop", signature, name, interfaces);
	}

	/**
	 * 访问方法
	 */
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ("sayHi".equals(name)) {
			return null;
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);// ,默认复制父类方法体
	}
}