package com.cduer.people.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import li.util.Convert;

import org.bee.tl.core.Config;
import org.bee.tl.core.GroupTemplate;
import org.bee.tl.core.Template;

public class Beetl {
	public static void main(String[] args) {
		System.out.println(new Beetl(new File("D:\\workspace\\liz\\WebContent\\WEB-INF\\view\\beetl.htm")).merge(Convert.toMap()));
	}

	private Template template;

	public Beetl(File file) {
		try {
			GroupTemplate groupTemplate = new Config().createGroupTemplate();
			groupTemplate.setStatementStart("<!---");
			groupTemplate.setStatementEnd("-->");
			template = groupTemplate.getReaderTemplate(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String merge(Map<?, ?> map) {
		try {
			for (Entry<?, ?> entry : map.entrySet()) {
				template.set(entry.getKey().toString(), entry.getValue());
			}
			return template.getTextAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}