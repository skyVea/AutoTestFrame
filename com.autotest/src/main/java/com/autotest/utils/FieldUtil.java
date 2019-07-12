package com.autotest.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Field工具
 * 
 * @author veaZhao
 *
 */
public class FieldUtil {
	/**
	 * @param clazz
	 * @return 获取所有Field包括父类
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> allFields = new ArrayList<Field>();
		Field[] subfields = clazz.getDeclaredFields();
		for (int i = 0; i < subfields.length; i++) {
			allFields.add(subfields[i]);
		}
		if (clazz.getSuperclass() != null) {
			allFields.addAll(getAllFields(clazz.getSuperclass()));
		}
		return allFields;
	}

}
