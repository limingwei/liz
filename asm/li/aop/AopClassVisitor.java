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
		if ("<init>".equals(name)) {
			return cv.visitMethod(access, name, desc, signature, exceptions);// 构造方法,复制父类方法体
		}
		return null;// 其他方法,直接返回空
	}

	/**
	 * 添加被Aop的方法
	 */
	public void visitEnd() {
		MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "sayHi", "(Ljava/lang/String;)V", null, null);
		methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		methodVisitor.visitLdcInsn("hello world by aop");
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0, 1);
	}
}