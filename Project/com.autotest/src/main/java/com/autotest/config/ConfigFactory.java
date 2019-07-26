package com.autotest.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.autotest.consts.GlobalConst;
import com.autotest.model.DataSourceConfig;
import com.autotest.model.TestModuleConfig;
import com.autotest.model.TestSheetConfig;
import com.autotest.model.TestSuitConfig;
import com.autotest.utils.PropertiesUtil;
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
		Element suitElement = XmlUtil.getUniqueChildElementNode("testsuit", document);
		List<Element> testmodules = XmlUtil.getChildElementNodes("testmodule", suitElement);
		TestSuitConfig testSuitConfig = new TestSuitConfig();
		List<TestModuleConfig> testModuleConfigs = new ArrayList<TestModuleConfig>();
		testSuitConfig.setTestModuleConfigs(testModuleConfigs);
		for (int i = 0; i < testmodules.size(); i++) {
			Element element = testmodules.get(i);
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
			List<Element> testSheetList = XmlUtil.getChildElementNodes("testsheet", element);
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

	public List<DataSourceConfig> getDataSourceConfig() {
		Document document = XmlUtil.readXML(GlobalConst.DSCONFIG);
		Element rootElement = XmlUtil.getUniqueChildElementNode("datasources", document);
		List<Element> dsElements = XmlUtil.getChildElementNodes("datasource", rootElement);
		List<DataSourceConfig> dataSourceConfigs = new ArrayList<DataSourceConfig>();
		for (int i = 0; i < dsElements.size(); i++) {
			Element dsElement = dsElements.get(i);
			DataSourceConfig dataSourceConfig = new DataSourceConfig();
			dataSourceConfigs.add(dataSourceConfig);
			Map<String, String> attrs = XmlUtil.getAttrs(dsElement);
			dataSourceConfig.setDbType(attrs.get("dbType"));
			dataSourceConfig.setId(attrs.get("id"));
			dataSourceConfig.setUrl(XmlUtil.getCDATAText("url", dsElement));
			dataSourceConfig.setPassword(XmlUtil.getText("password", dsElement));
			dataSourceConfig.setUser(XmlUtil.getText("username", dsElement));
			Element poolproties = XmlUtil.getUniqueChildElementNode("poolProperties", dsElement);
			Properties properties = new Properties();
			properties.setProperty("minPoolSize", XmlUtil.getText("minPoolSize", poolproties));
			properties.setProperty("maxPoolSize", XmlUtil.getText("maxPoolSize", poolproties));
			properties.setProperty("maxIdleTime", XmlUtil.getText("maxIdleTime", poolproties));
			properties.setProperty("acquisitionTimeout", XmlUtil.getText("acquisitionTimeout", poolproties));
			properties.setProperty("shareTransactionConnections",
					XmlUtil.getText("shareTransactionConnections", poolproties));
			properties.setProperty("acquireIncrement", XmlUtil.getText("acquireIncrement", poolproties));
			properties.setProperty("deferConnectionRelease", XmlUtil.getText("deferConnectionRelease", poolproties));
			properties.setProperty("testQuery", XmlUtil.getText("testQuery", poolproties));
			dataSourceConfig.setPoolProperties(properties);
		}
		return dataSourceConfigs;
	}

}
