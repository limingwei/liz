package li.aop;

public class AopClassLoader extends ClassLoader {
	public Class<?> defineClass(String type, byte[] bytes) throws ClassFormatError {
		return super.defineClass(type + "$Aop", bytes, 0, bytes.length);
	}
}
