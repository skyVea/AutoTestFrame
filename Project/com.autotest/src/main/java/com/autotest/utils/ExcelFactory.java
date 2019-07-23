package com.autotest.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Excel工厂
 * 
 * @author veaZhao
 *
 */
public class ExcelFactory {
	public static ExcelFactory excelfactory = null;
	final private static Map<String, ExcelUtil> CACHEBOOK = new HashMap<String, ExcelUtil>();

	private ExcelFactory() {

	}

	public static synchronized ExcelFactory getInstance() {
		if (null == excelfactory) {
			excelfactory = new ExcelFactory();
		}
		return excelfactory;
	}

	public ExcelUtil getExcelUtil(String pathname) {
		if (CACHEBOOK.containsKey(pathname)) {
			return CACHEBOOK.get(pathname);
		} else {
			ExcelUtil excelUtil = new ExcelUtil(pathname);
			CACHEBOOK.put(pathname, excelUtil);
			return excelUtil;
		}
	}

}
