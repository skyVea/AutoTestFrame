package com.autotest.dao;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.hibernate.cfg.Configuration;

import com.autotest.model.DataSourceConfig;
import com.autotest.utils.PathUtil;

/**
 * @author veaZhao 通过指定datasourceid，构建数据源配置对象
 */
public class HibernateConfig {
	private Configuration conf = null;
	private DataSourceConfig dsConfig = null;

	public HibernateConfig(DataSourceConfig dsconfig) {
		this.dsConfig = dsconfig;
		conf = new Configuration();
		Properties properties = new Properties(dsconfig.getPoolProperties());
		properties.setProperty("hibernate.connection.url", dsconfig.getUrl());
		properties.setProperty("hibernate.connection.username", dsconfig.getUser());
		properties.setProperty("hibernate.connection.password", dsconfig.getPassword());
		// properties.setProperty("hibernate.current_session_context_class",
		// "org.springframework.orm.hibernate5.SpringSessionContext");
		properties.setProperty("hibernate.current_session_context_class", "thread");
		// properties.setProperty("mappingLocations", "classpath:**/vo/*.hbm.xml");
		// properties.setProperty("mappingLocations",
		// "classpath:TestDemo/vo/TestAddressVO.hbm.xml");
		// properties.setProperty("hibernate.transaction.coordinator_class", "jta");
		Set<String> urls = PathUtil.getURLFromPkg("", ".hbm.xml");
		for (Iterator iterator = urls.iterator(); iterator.hasNext();) {
			String url = (String) iterator.next();
			conf.addResource(PathUtil.getPKGPath(url));
		}
		conf.setProperties(properties);
	}

	public void setProperty(String propertyName, String value) {
		conf.setProperty(propertyName, value);
	}

	public Configuration getConfig() {
		return conf;
	}

	public DataSourceConfig getDsConfig() {
		return dsConfig;
	}

	public void setDsConfig(DataSourceConfig dsConfig) {
		this.dsConfig = dsConfig;
	}

}
