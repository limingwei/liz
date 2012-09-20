package li.ioc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import li.annotation.Inject;
import li.model.Bean;
import li.util.Files;
import li.util.Log;
import li.util.Reflect;

/**
 * Ioc加载器,加载用注解方式配置的Bean
 * 
 * @author li (limw@w.cn)
 * @version 0.1.2 (2012-05-08)
 */
public class AnnotationIocLoader {
	private static final Log log = Log.init();

	/**
	 * 扫描 Source Floder 下的所有类文件, 将其中加了@Bean注解的类返回,然后被加入到IocContext
	 */
	public List<Bean> getBeans() {
		List<String> fileList = Files.list(Files.root(), "^.*.class$", true);
		log.info(String.format("Found %s class files, at %s", fileList.size(), Files.root()));

		List<li.model.Bean> beans = new ArrayList<li.model.Bean>();
		for (String classFile : fileList) {
			String className = classFile.split("\\\\classes\\\\")[1].replaceAll("\\\\", ".").replace(".class", "");// 取/classes/之后的字符串,替换/为.,去掉.class
			Class<?> type = Reflect.getType(className);
			li.annotation.Bean beanAnnotation = (li.annotation.Bean) type.getAnnotation(li.annotation.Bean.class);
			if (beanAnnotation != null) {
				li.model.Bean iocBean = new li.model.Bean();
				iocBean.type = type;
				iocBean.name = beanAnnotation.value();

				for (Field field : type.getDeclaredFields()) {
					Inject inject = field.getAnnotation(Inject.class);
					if (null != inject) {
						li.model.Field attribute = new li.model.Field();
						attribute.name = field.getName();
						attribute.type = field.getType();
						attribute.value = inject.value();
						iocBean.fields.add(attribute);
					}
				}
				beans.add(iocBean);

				log.info(String.format("ADD BEAN: @Bean %s %s", type.getName(), iocBean.name));
			}
		}
		return beans;
	}
}