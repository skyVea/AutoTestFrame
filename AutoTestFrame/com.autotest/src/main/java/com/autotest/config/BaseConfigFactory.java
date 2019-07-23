package com.autotest.config;

/**
 * 配置文件工厂
 *
 * @author veaZhao
 *
 */
public abstract class BaseConfigFactory {

	/**
	 * 获取配置对象
	 * 
	 * @param filePath
	 *            文件路径
	 * @param attribute
	 *            附加参数，不需要可以不传如："sheetName"
	 * @return
	 */
	public Object getConfig(String filePath, Object attribute) {
		if (filePath.endsWith(".xml")) {

		}
		if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {

		}
		if (filePath.endsWith(".properties")) {

		}
		return null;
	}
}
