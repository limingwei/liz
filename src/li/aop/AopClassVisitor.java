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
		System.err.println(access + "\t" + name + "\t" + desc + "\t" + signature + "\t" + exceptions);
		if ("<init>".equals(name)) {// 初始化方法,传递父类的方法
			return cv.visitMethod(access, name, desc, signature, exceptions);
		} else if ("sayHi".equals(name)) {
			MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, name, "()V", null, null);
			methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");// 插入Aop代码
			methodVisitor.visitLdcInsn("Aop插入的newMethod代码");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

			return methodVisitor;
		} else {
			return null;// 不返回方法,默认调用父类方法
		}
	}

	public void visitEnd() {// 新增加的方法
		MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "newMethod", "()V", null, null);
		methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");// 插入Aop代码
		methodVisitor.visitLdcInsn("Aop插入的newMethod代码");
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0, 1);
	}
}