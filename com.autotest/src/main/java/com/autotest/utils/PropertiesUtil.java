package com.autotest.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 解析-properties文件
 * @author veaZhao
 *
 */
public class PropertiesUtil {

	public static Map<Object, Object> analysisProperties(String filePath) {
		Properties prop = new Properties();
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(filePath));
			try {
				prop.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Map<Object, Object> map = new HashMap<Object, Object>();
		Iterator<?> it = prop.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			map.put(key, value);
		}
		return map;
	}
}