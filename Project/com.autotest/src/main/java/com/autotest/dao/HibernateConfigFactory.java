package com.autotest.dao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.autotest.config.ConfigFactory;
import com.autotest.model.DataSourceConfig;

public class HibernateConfigFactory {
	private static HibernateConfigFactory hibernateConfigFactory = null;
	private static ConcurrentHashMap<String, HibernateConfig> CONFIGMAP = new ConcurrentHashMap<String, HibernateConfig>();

	public static synchronized HibernateConfigFactory getInstance() {
		if (hibernateConfigFactory == null) {
			hibernateConfigFactory = new HibernateConfigFactory();
			List<DataSourceConfig> dataSourceConfigs = ConfigFactory.getInstance().getDataSourceConfig();
			for (int i = 0; i < dataSourceConfigs.size(); i++) {
				HibernateConfig hibernateConfig = new HibernateConfig(dataSourceConfigs.get(i));
				CONFIGMAP.putIfAbsent(dataSourceConfigs.get(i).getId(), hibernateConfig);
			}
		}
		return hibernateConfigFactory;
	}

	public HibernateConfig getConfig(String dsid) {
		return CONFIGMAP.get(dsid);
	}
}
