package li.ioc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import li.model.Bean;
import li.model.Field;
import li.util.Files;
import li.util.Log;
import li.util.Reflect;

import org.w3c.dom.NodeList;

/**
 * Ioc加载器,加载用Xml文件配置的Bean
 * 
 * @author li (limw@w.cn)
 * @version 0.1.4 (2012-05-08)
 */
public class XmlIocLoader {
	private static final Log log = Log.init();

	/**
	 * 解析SourceFloder下搜索到的文件名以config.xml结尾的文件,将其中配置的Bean返回
	 */
	public List<Bean> getBeans() {
		List<String> fileList = Files.list(Files.root(), "^.*config.xml$", true);// 搜索以config.xml结尾的文件
		log.info(String.format("Found %s Xml config files,at %s", fileList.size(), Files.root()));

		List<Bean> beans = new ArrayList<Bean>();
		for (String filePath : fileList) {
			NodeList beanNodes = (NodeList) Files.xpath(Files.build(filePath), "//bean", XPathConstants.NODESET);
			for (int i = 0; null != beanNodes && i < beanNodes.getLength(); i++) {
				Bean iocBean = new Bean();
				iocBean.type = Reflect.getType(Files.xpath(beanNodes.item(i), "@class", XPathConstants.STRING).toString());
				iocBean.name = Files.xpath(beanNodes.item(i), "@name", XPathConstants.STRING).toString();

				NodeList propertyNodes = (NodeList) Files.xpath(beanNodes.item(i), "property", XPathConstants.NODESET);
				for (int m = 0; null != propertyNodes && m < propertyNodes.getLength(); m++) {
					Field field = new Field();
					field.name = (String) Files.xpath(propertyNodes.item(m), "@name", XPathConstants.STRING);
					field.type = Reflect.fieldType(iocBean.type, field.name);
					field.value = (String) Files.xpath(propertyNodes.item(m), "@value", XPathConstants.STRING);
					iocBean.fields.add(field);
				}
				beans.add(iocBean);

				log.info(String.format("ADD BEAN: XML %s %s", iocBean.type.getName(), iocBean.name));
			}
		}
		return beans;
	}
}