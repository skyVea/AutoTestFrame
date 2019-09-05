package com.autotest.dao;

import org.hibernate.Session;

public interface TryCatchCallBack<T> {
	T execute(Session session);
}
