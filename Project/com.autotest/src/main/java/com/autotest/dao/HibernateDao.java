package com.autotest.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class HibernateDao implements Dao {
	private HibernateTemplate hibernateTemplate = null;

	public HibernateDao(String dsid) {
		HibernateConfig config = HibernateConfigFactory.getInstance().getConfig(dsid);
		SessionFactory sessionFactory = config.getConfig().buildSessionFactory();
		setSessionFactory(sessionFactory);
	}

	public HibernateDao(HibernateConfig config) {
		SessionFactory sessionFactory = config.getConfig().buildSessionFactory();
		setSessionFactory(sessionFactory);
	}

	public List find(String queryString, Object... values) {
		List list = hibernateTemplate.find(queryString, values);
		return list;
	}

	public void save(Object object) {
		hibernateTemplate.save(object);
	}

	public void saveOrUpdate(Object object) {
		hibernateTemplate.saveOrUpdate(object);
	}

	public void delete(Object object) {
		hibernateTemplate.delete(object);
	}

	public Object findById(Class<?> clazz, String id) {
		Object vo = null;
		vo = this.hibernateTemplate.get(clazz, id);
		return vo;
	}
	
	public List<Object> findAll(Class<?> clazz){
		return this.hibernateTemplate.executeWithNativeSession(session -> {
			return this.hibernateTemplate.protectSession(protectsession -> {
				List listtemp = null;
				Criteria criteria = protectsession.createCriteria(clazz);
				listtemp = criteria.list();
				return listtemp;
			}, session);
		});
	}
	
	public List execute(String sql) {
		return this.hibernateTemplate.executeWithNativeSession(session -> {
			return this.hibernateTemplate.protectSession(protectsession -> {
				List listtemp = null;
				SQLQuery query = protectsession.createSQLQuery(sql);
				listtemp = query.list();
				return listtemp;
			}, session);
		});
	}

	public SessionFactory getSessionFactory() {
		SessionFactory sessionFactory = (this.hibernateTemplate != null ? this.hibernateTemplate.getSessionFactory()
				: null);
		return sessionFactory;
	}

	/**
	 * 获取sessionFactory
	 * 
	 * @param sessionFactory
	 *            sessionFactory实例
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		if (this.hibernateTemplate == null || sessionFactory != this.hibernateTemplate.getSessionFactory()) {
			this.hibernateTemplate = createHibernateTemplate(sessionFactory);
		}
	}

	HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		HibernateTemplate hibernateTemplate = null;
		hibernateTemplate = new HibernateTemplate(sessionFactory);
		return hibernateTemplate;
	}

}
