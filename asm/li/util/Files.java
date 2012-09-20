package li.util;

import java.io.File;
import java.io.FileOutputStream;

public class Files {
	public static void write(File file, byte[] bytes) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes);
			fileOutputStream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(Class<?> type, byte[] bytes) {
		File file = new File("D:\\Users\\li\\Desktop\\" + type.getName() + ".class");
		write(file, bytes);
	}
}
