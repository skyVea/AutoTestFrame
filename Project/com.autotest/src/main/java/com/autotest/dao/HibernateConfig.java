package com.autotest.dao;

import java.util.Properties;

import org.hibernate.cfg.Configuration;

import com.autotest.model.DataSourceConfig;

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
		properties.setProperty("hibernate.current_session_context_class", "jta");
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
