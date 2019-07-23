package com.autotest.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.autotest.consts.GlobalConst;
import com.autotest.model.TestModuleConfig;
import com.autotest.model.TestSheetConfig;
import com.autotest.model.TestSuitConfig;
import com.autotest.utils.StringUtils;
import com.autotest.utils.XmlUtil;

/**
 * 配置文件工厂
 * 
 * @author veaZhao
 *
 */
public class ConfigFactory extends BaseConfigFactory {
	private static ConfigFactory configFactory = null;

	public static ConfigFactory getInstance() {
		if (configFactory == null) {
			configFactory = new ConfigFactory();
		}
		return configFactory;
	}

	/**
	 * @return 获取TestSuitConfig
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public TestSuitConfig getSuitConfig() {
		Document document = XmlUtil.readXML(GlobalConst.TESTSUIT);
		List<Element> testSuitList = XmlUtil.getRootElementNodes(document);
		List<Element> testModuleList = XmlUtil.getSubElementNodeByName(testSuitList.get(0), "testmodule");
		TestSuitConfig testSuitConfig = new TestSuitConfig();
		List<TestModuleConfig> testModuleConfigs = new ArrayList<TestModuleConfig>();
		testSuitConfig.setTestModuleConfigs(testModuleConfigs);
		for (int i = 0; i < testModuleList.size(); i++) {
			Element element = testModuleList.get(i);
			TestModuleConfig testModuleConfig = new TestModuleConfig();
			Field[] fields = TestModuleConfig.class.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				String value = element.getAttribute(fields[j].getName());
				if (!StringUtils.isEmpty(value)) {
					fields[j].setAccessible(true);
					try {
						fields[j].set(testModuleConfig, value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			List<Element> testSheetList = XmlUtil.getSubElementNodes(element);
			List<TestSheetConfig> testSheetConfigs = new ArrayList<TestSheetConfig>();
			for (int j = 0; j < testSheetList.size(); j++) {
				TestSheetConfig testSheetConfig = new TestSheetConfig();
				Field[] fieldsheet = TestSheetConfig.class.getDeclaredFields();
				Element shElement = testSheetList.get(j);
				for (int k = 0; k < fieldsheet.length; k++) {
					String value = shElement.getAttribute(fieldsheet[k].getName());
					if (!StringUtils.isEmpty(value)) {
						fieldsheet[k].setAccessible(true);
						try {
							fieldsheet[k].set(testSheetConfig, value);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				testSheetConfigs.add(testSheetConfig);
			}
			testModuleConfig.setTestSheetConfigs(testSheetConfigs);
			testModuleConfigs.add(testModuleConfig);
		}
		return testSuitConfig;
	}

}
