package li.aop;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AopMethodVisitor extends MethodVisitor implements Opcodes {
	public AopMethodVisitor() {
		super(ASM4);
	}
}