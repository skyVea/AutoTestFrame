package com.autotest.dao;

import java.util.List;

import org.hibernate.SessionFactory;


public abstract interface Dao {

	public List find(String queryString, Object... values);

	public void save(Object object);

	public void saveOrUpdate(Object object);

	public void delete(Object object);

	public Object findById(Class<?> clazz, String id);
	
	public List findAll(Class<?> clazz);

	public List execute(String sql);

	public SessionFactory getSessionFactory();
}
