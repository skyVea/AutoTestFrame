package com.autotest.dao;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author veaZhao 获取HibernateDao
 */
public class HibernateDaoFactory {
	public static HibernateDaoFactory hibernateDaoFactory = null;
	public static ConcurrentHashMap<String, Dao> DAOMAP = new ConcurrentHashMap<String, Dao>();

	public static synchronized HibernateDaoFactory getInstance() {
		if (hibernateDaoFactory == null) {
			hibernateDaoFactory = new HibernateDaoFactory();
		}
		return hibernateDaoFactory;
	}

	public Dao getDao(String dsid) {
		Dao dao = DAOMAP.get(dsid);
		if (dao == null) {
			dao = new HibernateDao(dsid);
			DAOMAP.put(dsid, dao);
		}
		return DAOMAP.get(dsid);
	}

	public Dao getDao(HibernateConfig config) {
		String dsid = config.getDsConfig().getId();
		return getDao(dsid);
	}

	@Deprecated
	public Dao getDao(File configfile) {
		return null;
	}

}
