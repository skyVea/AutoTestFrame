
package com.autotest.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.HibernateCallback;

public class HibernateTemplate {

	private static final Method createQueryMethod;

	private static final Method getNamedQueryMethod;

	static {
		try {
			createQueryMethod = Session.class.getMethod("createQuery", String.class);
			getNamedQueryMethod = Session.class.getMethod("getNamedQuery", String.class);
		} catch (NoSuchMethodException ex) {
			throw new IllegalStateException("Incompatible Hibernate Session API", ex);
		}
	}

	protected final Log logger = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	private String[] filterNames;

	private boolean exposeNativeSession = false;

	private boolean checkWriteOperations = true;

	private boolean cacheQueries = false;

	private String queryCacheRegion;

	private int fetchSize = 0;

	private int maxResults = 0;

	/**
	 * Create a new HibernateTemplate instance.
	 */
	public HibernateTemplate() {
	}

	/**
	 * Create a new HibernateTemplate instance.
	 * 
	 * @param sessionFactory
	 *            the SessionFactory to create Sessions with
	 */
	public HibernateTemplate(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
		afterPropertiesSet();
	}

	/**
	 * Set the Hibernate SessionFactory that should be used to create Hibernate
	 * Sessions.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Return the Hibernate SessionFactory that should be used to create Hibernate
	 * Sessions.
	 */

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
	 * Obtain the SessionFactory for actual use.
	 * 
	 * @return the SessionFactory (never {@code null})
	 * @throws IllegalStateException
	 *             in case of no SessionFactory set
	 * @since 5.0
	 */
	protected final SessionFactory obtainSessionFactory() {
		SessionFactory sessionFactory = getSessionFactory();
		return sessionFactory;
	}

	/**
	 * Set one or more names of Hibernate filters to be activated for all Sessions
	 * that this accessor works with.
	 * <p>
	 * Each of those filters will be enabled at the beginning of each operation and
	 * correspondingly disabled at the end of the operation. This will work for
	 * newly opened Sessions as well as for existing Sessions (for example, within a
	 * transaction).
	 * 
	 * @see #enableFilters(Session)
	 * @see Session#enableFilter(String)
	 */
	public void setFilterNames(String... filterNames) {
		this.filterNames = filterNames;
	}

	/**
	 * Return the names of Hibernate filters to be activated, if any.
	 */

	public String[] getFilterNames() {
		return this.filterNames;
	}

	/**
	 * Set whether to expose the native Hibernate Session to HibernateCallback code.
	 * <p>
	 * Default is "false": a Session proxy will be returned, suppressing
	 * {@code close} calls and automatically applying query cache settings and
	 * transaction timeouts.
	 * 
	 * @see HibernateCallback
	 * @see Session
	 * @see #setCacheQueries
	 * @see #setQueryCacheRegion
	 * @see #prepareQuery
	 * @see #prepareCriteria
	 */
	public void setExposeNativeSession(boolean exposeNativeSession) {
		this.exposeNativeSession = exposeNativeSession;
	}

	/**
	 * Return whether to expose the native Hibernate Session to HibernateCallback
	 * code, or rather a Session proxy.
	 */
	public boolean isExposeNativeSession() {
		return this.exposeNativeSession;
	}

	/**
	 * Set whether to check that the Hibernate Session is not in read-only mode in
	 * case of write operations (save/update/delete).
	 * <p>
	 * Default is "true", for fail-fast behavior when attempting write operations
	 * within a read-only transaction. Turn this off to allow save/update/delete on
	 * a Session with flush mode MANUAL.
	 * 
	 * @see #checkWriteOperationAllowed
	 * @see org.springframework.transaction.TransactionDefinition#isReadOnly
	 */
	public void setCheckWriteOperations(boolean checkWriteOperations) {
		this.checkWriteOperations = checkWriteOperations;
	}

	/**
	 * Return whether to check that the Hibernate Session is not in read-only mode
	 * in case of write operations (save/update/delete).
	 */
	public boolean isCheckWriteOperations() {
		return this.checkWriteOperations;
	}

	/**
	 * Set whether to cache all queries executed by this template.
	 * <p>
	 * If this is "true", all Query and Criteria objects created by this template
	 * will be marked as cacheable (including all queries through find methods).
	 * <p>
	 * To specify the query region to be used for queries cached by this template,
	 * set the "queryCacheRegion" property.
	 * 
	 * @see #setQueryCacheRegion
	 * @see org.hibernate.Query#setCacheable
	 * @see Criteria#setCacheable
	 */
	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	/**
	 * Return whether to cache all queries executed by this template.
	 */
	public boolean isCacheQueries() {
		return this.cacheQueries;
	}

	/**
	 * Set the name of the cache region for queries executed by this template.
	 * <p>
	 * If this is specified, it will be applied to all Query and Criteria objects
	 * created by this template (including all queries through find methods).
	 * <p>
	 * The cache region will not take effect unless queries created by this template
	 * are configured to be cached via the "cacheQueries" property.
	 * 
	 * @see #setCacheQueries
	 * @see org.hibernate.Query#setCacheRegion
	 * @see Criteria#setCacheRegion
	 */
	public void setQueryCacheRegion(String queryCacheRegion) {
		this.queryCacheRegion = queryCacheRegion;
	}

	/**
	 * Return the name of the cache region for queries executed by this template.
	 */

	public String getQueryCacheRegion() {
		return this.queryCacheRegion;
	}

	/**
	 * Set the fetch size for this HibernateTemplate. This is important for
	 * processing large result sets: Setting this higher than the default value will
	 * increase processing speed at the cost of memory consumption; setting this
	 * lower can avoid transferring row data that will never be read by the
	 * application.
	 * <p>
	 * Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Return the fetch size specified for this HibernateTemplate.
	 */
	public int getFetchSize() {
		return this.fetchSize;
	}

	/**
	 * Set the maximum number of rows for this HibernateTemplate. This is important
	 * for processing subsets of large result sets, avoiding to read and hold the
	 * entire result set in the database or in the JDBC driver if we're never
	 * interested in the entire result in the first place (for example, when
	 * performing searches that might return a large number of matches).
	 * <p>
	 * Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Return the maximum number of rows specified for this HibernateTemplate.
	 */
	public int getMaxResults() {
		return this.maxResults;
	}

	public void afterPropertiesSet() {
		if (getSessionFactory() == null) {
			throw new IllegalArgumentException("Property 'sessionFactory' is required");
		}
	}

	public <T> T execute(HibernateCallback<T> action) {
		return doExecute(action, false);
	}

	/**
	 * Execute the action specified by the given action object within a native
	 * {@link Session}.
	 * <p>
	 * This execute variant overrides the template-wide
	 * {@link #isExposeNativeSession() "exposeNativeSession"} setting.
	 * 
	 * @param action
	 *            callback object that specifies the Hibernate action
	 * @return a result object returned by the action, or {@code null} @ in case of
	 *         Hibernate errors
	 */

	public <T> T executeWithNativeSession(HibernateCallback<T> action) {
		return doExecute(action, true);
	}

	/**
	 * Execute the action specified by the given action object within a Session.
	 * 
	 * @param action
	 *            callback object that specifies the Hibernate action
	 * @param enforceNativeSession
	 *            whether to enforce exposure of the native Hibernate Session to
	 *            callback code
	 * @return a result object returned by the action, or {@code null} @ in case of
	 *         Hibernate errors
	 */

	protected <T> T doExecute(HibernateCallback<T> action, boolean enforceNativeSession) {

		Session session = null;
		boolean isNew = false;
		try {
			session = obtainSessionFactory().getCurrentSession();
		} catch (HibernateException ex) {
			logger.debug("Could not retrieve pre-bound Hibernate session", ex);
		}
		if (session == null) {
			session = obtainSessionFactory().openSession();
			session.setFlushMode(FlushMode.MANUAL);
			isNew = true;
		}

		try {
			enableFilters(session);
			Session sessionToExpose = (enforceNativeSession || isExposeNativeSession() ? session
					: createSessionProxy(session));
			return action.doInHibernate(sessionToExpose);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (isNew) {
				session.close();
			} else {
				disableFilters(session);
			}
		}
	}

	/**
	 * Create a close-suppressing proxy for the given Hibernate Session. The proxy
	 * also prepares returned Query and Criteria objects.
	 * 
	 * @param session
	 *            the Hibernate Session to create a proxy for
	 * @return the Session proxy
	 * @see Session#close()
	 * @see #prepareQuery
	 * @see #prepareCriteria
	 */
	protected Session createSessionProxy(Session session) {
		return (Session) Proxy.newProxyInstance(session.getClass().getClassLoader(), new Class<?>[] { Session.class },
				new CloseSuppressingInvocationHandler(session));
	}

	/**
	 * Enable the specified filters on the given Session.
	 * 
	 * @param session
	 *            the current Hibernate Session
	 * @see #setFilterNames
	 * @see Session#enableFilter(String)
	 */
	protected void enableFilters(Session session) {
		String[] filterNames = getFilterNames();
		if (filterNames != null) {
			for (String filterName : filterNames) {
				session.enableFilter(filterName);
			}
		}
	}

	/**
	 * Disable the specified filters on the given Session.
	 * 
	 * @param session
	 *            the current Hibernate Session
	 * @see #setFilterNames
	 * @see Session#disableFilter(String)
	 */
	protected void disableFilters(Session session) {
		String[] filterNames = getFilterNames();
		if (filterNames != null) {
			for (String filterName : filterNames) {
				session.disableFilter(filterName);
			}
		}
	}

	// -------------------------------------------------------------------------
	// Convenience methods for loading individual objects
	// -------------------------------------------------------------------------

	public <T> T get(Class<T> entityClass, Serializable id) {
		return get(entityClass, id, null);
	}

	public <T> T get(final Class<T> entityClass, final Serializable id, final LockMode lockMode) {

		// return executeWithNativeSession(session -> {
		// if (lockMode != null) {
		// return session.get(entityClass, id, new LockOptions(lockMode));
		// } else {
		// return session.get(entityClass, id);
		// }
		// });

		return executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				if (lockMode != null) {
					return protectsession.get(entityClass, id, new LockOptions(lockMode));
				} else {
					return protectsession.get(entityClass, id);
				}
			}, session);
		});
	}

	public Object get(String entityName, Serializable id) {
		return get(entityName, id, null);
	}

	public Object get(final String entityName, final Serializable id, final LockMode lockMode) {

		// return executeWithNativeSession(session -> {
		// if (lockMode != null) {
		// return session.get(entityName, id, new LockOptions(lockMode));
		// } else {
		// return session.get(entityName, id);
		// }
		// });

		return executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				if (lockMode != null) {
					return protectsession.get(entityName, id, new LockOptions(lockMode));
				} else {
					return protectsession.get(entityName, id);
				}
			}, session);
		});
	}

	public <T> T load(Class<T> entityClass, Serializable id) {
		return load(entityClass, id, null);
	}

	public <T> T load(final Class<T> entityClass, final Serializable id, final LockMode lockMode) {

		return executeWithNativeSession(session -> {
			if (lockMode != null) {
				return session.load(entityClass, id, new LockOptions(lockMode));
			} else {
				return session.load(entityClass, id);
			}
		});
	}

	public Object load(String entityName, Serializable id) {
		return load(entityName, id, null);
	}

	public Object load(final String entityName, final Serializable id, final LockMode lockMode) {

		return executeWithNativeSession(session -> {
			if (lockMode != null) {
				return session.load(entityName, id, new LockOptions(lockMode));
			} else {
				return session.load(entityName, id);
			}
		});
	}

	public <T> List<T> loadAll(final Class<T> entityClass) {
		return executeWithNativeSession((HibernateCallback<List<T>>) session -> {
			Criteria criteria = session.createCriteria(entityClass);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			prepareCriteria(criteria);
			return criteria.list();
		});
	}

	public void load(final Object entity, final Serializable id) {
		executeWithNativeSession(session -> {
			session.load(entity, id);
			return null;
		});
	}

	public void refresh(final Object entity) {
		refresh(entity, null);
	}

	public void refresh(final Object entity, final LockMode lockMode) {
		executeWithNativeSession(session -> {
			if (lockMode != null) {
				session.refresh(entity, new LockOptions(lockMode));
			} else {
				session.refresh(entity);
			}
			return null;
		});
	}

	public boolean contains(final Object entity) {
		Boolean result = executeWithNativeSession(session -> session.contains(entity));
		return result;
	}

	public void evict(final Object entity) {
		executeWithNativeSession(session -> {
			session.evict(entity);
			return null;
		});
	}

	public void initialize(Object proxy) {
		try {
			Hibernate.initialize(proxy);
		} catch (HibernateException ex) {
			throw ex;
		}
	}

	public Filter enableFilter(String filterName) throws IllegalStateException {
		Session session = obtainSessionFactory().getCurrentSession();
		Filter filter = session.getEnabledFilter(filterName);
		if (filter == null) {
			filter = session.enableFilter(filterName);
		}
		return filter;
	}

	// -------------------------------------------------------------------------
	// Convenience methods for storing individual objects
	// -------------------------------------------------------------------------

	public void lock(final Object entity, final LockMode lockMode) {
		executeWithNativeSession(session -> {
			session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
			return null;
		});
	}

	public void lock(final String entityName, final Object entity, final LockMode lockMode) {
		executeWithNativeSession(session -> {
			session.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
			return null;
		});
	}

	public Serializable save(final Object entity) {
		// return executeWithNativeSession(session -> {
		// return protectSession(new TryCatchCallBack<Serializable>() {
		// @Override
		// public Serializable execute(Session session) {
		// return session.save(entity);
		// }
		// }, session);
		// });
		return executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				return protectsession.save(entity);
			}, session);
		});
	}

	/**
	 * @param callBack
	 *            为数据操作捕获异常
	 * @param session
	 * @return
	 */
	public <T> T protectSession(TryCatchCallBack<T> callBack, Session session) {
		T serializable = null;
		Transaction t = null;
		try {
			t = session.beginTransaction();
			t.begin();
			serializable = callBack.execute(session);
			t.commit();
		} catch (Exception e) {
			if (t != null) {
				t.rollback();
			}
			throw e;
		}
		return serializable;
	}

	public Serializable save(final String entityName, final Object entity) {
		// return executeWithNativeSession(session -> {
		// return session.save(entityName, entity);
		// });

		return executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				return protectsession.save(entityName, entity);
			}, session);
		});
	}

	public void update(Object entity) {
		update(entity, null);
	}

	public void update(final Object entity, final LockMode lockMode) {
		// executeWithNativeSession(session -> {
		// session.update(entity);
		// if (lockMode != null) {
		// session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
		// }
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.update(entity);
				if (lockMode != null) {
					protectsession.buildLockRequest(new LockOptions(lockMode)).lock(entity);
				}
				return null;
			}, session);
		});
	}

	public void update(String entityName, Object entity) {
		update(entityName, entity, null);
	}

	public void update(final String entityName, final Object entity, final LockMode lockMode) {

		// executeWithNativeSession(session -> {
		// session.update(entityName, entity);
		// if (lockMode != null) {
		// session.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
		// }
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.update(entityName, entity);
				if (lockMode != null) {
					protectsession.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
				}
				return null;
			}, session);
		});
	}

	public void saveOrUpdate(final Object entity) {
		// executeWithNativeSession(session -> {
		// session.saveOrUpdate(entity);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.saveOrUpdate(entity);
				return null;
			}, session);
		});
	}

	public void saveOrUpdate(final String entityName, final Object entity) {
		// executeWithNativeSession(session -> {
		// session.saveOrUpdate(entityName, entity);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				session.saveOrUpdate(entityName, entity);
				return null;
			}, session);
		});
	}

	public void replicate(final Object entity, final ReplicationMode replicationMode) {

		// executeWithNativeSession(session -> {
		// session.replicate(entity, replicationMode);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.replicate(entity, replicationMode);
				return null;
			}, session);
		});
	}

	public void replicate(final String entityName, final Object entity, final ReplicationMode replicationMode) {

		// executeWithNativeSession(session -> {
		// session.replicate(entityName, entity, replicationMode);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.replicate(entityName, entity, replicationMode);
				return null;
			}, session);
		});
	}

	public void persist(final Object entity) {
		// executeWithNativeSession(session -> {
		// session.persist(entity);
		// return null;
		// });
		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.persist(entity);
				return null;
			}, session);
		});
	}

	public void persist(final String entityName, final Object entity) {
		// executeWithNativeSession(session -> {
		// session.persist(entityName, entity);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				protectsession.persist(entityName, entity);
				return null;
			}, session);
		});
	}

	@SuppressWarnings("unchecked")
	public <T> T merge(final T entity) {
		// return executeWithNativeSession(session -> {
		// return (T) session.merge(entity);
		// });

		return executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				return (T) protectsession.merge(entity);
			}, session);
		});
	}

	@SuppressWarnings("unchecked")
	public <T> T merge(final String entityName, final T entity) {
		// return executeWithNativeSession(session -> {
		// return (T) session.merge(entityName, entity);
		// });

		return executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				return (T) protectsession.merge(entityName, entity);
			}, session);
		});
	}

	public void delete(Object entity) {
		delete(entity, null);
	}

	public void delete(final Object entity, final LockMode lockMode) {
		// executeWithNativeSession(session -> {
		// if (lockMode != null) {
		// session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
		// }
		// session.delete(entity);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				if (lockMode != null) {
					protectsession.buildLockRequest(new LockOptions(lockMode)).lock(entity);
				}
				protectsession.delete(entity);
				return null;
			}, session);
		});
	}

	public void delete(String entityName, Object entity) {
		delete(entityName, entity, null);
	}

	public void delete(final String entityName, final Object entity, final LockMode lockMode) {

		// executeWithNativeSession(session -> {
		// if (lockMode != null) {
		// session.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
		// }
		// session.delete(entityName, entity);
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				if (lockMode != null) {
					protectsession.buildLockRequest(new LockOptions(lockMode)).lock(entityName, entity);
				}
				protectsession.delete(entityName, entity);
				return null;
			}, session);
		});
	}

	public void deleteAll(final Collection<?> entities) {
		// executeWithNativeSession(session -> {
		// for (Object entity : entities) {
		// session.delete(entity);
		// }
		// return null;
		// });

		executeWithNativeSession(session -> {
			return protectSession(protectsession -> {
				for (Object entity : entities) {
					protectsession.delete(entity);
				}
				return null;
			}, session);
		});
	}

	public void flush() {
		executeWithNativeSession(session -> {
			session.flush();
			return null;
		});

	}

	public void clear() {
		executeWithNativeSession(session -> {
			session.clear();
			return null;
		});
	}

	// -------------------------------------------------------------------------
	// Convenience finder methods for detached criteria
	// -------------------------------------------------------------------------

	public List<?> findByCriteria(DetachedCriteria criteria) {
		return findByCriteria(criteria, -1, -1);
	}

	public List<?> findByCriteria(final DetachedCriteria criteria, final int firstResult, final int maxResults) {

		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			Criteria executableCriteria = criteria.getExecutableCriteria(session);
			prepareCriteria(executableCriteria);
			if (firstResult >= 0) {
				executableCriteria.setFirstResult(firstResult);
			}
			if (maxResults > 0) {
				executableCriteria.setMaxResults(maxResults);
			}
			return executableCriteria.list();
		});

	}

	public <T> List<T> findByExample(T exampleEntity) {
		return findByExample(null, exampleEntity, -1, -1);
	}

	public <T> List<T> findByExample(String entityName, T exampleEntity) {
		return findByExample(entityName, exampleEntity, -1, -1);
	}

	public <T> List<T> findByExample(T exampleEntity, int firstResult, int maxResults) {
		return findByExample(null, exampleEntity, firstResult, maxResults);
	}

	public <T> List<T> findByExample(final String entityName, final T exampleEntity, final int firstResult,
			final int maxResults) {

		return executeWithNativeSession((HibernateCallback<List<T>>) session -> {
			Criteria executableCriteria = (entityName != null ? session.createCriteria(entityName)
					: session.createCriteria(exampleEntity.getClass()));
			executableCriteria.add(Example.create(exampleEntity));
			prepareCriteria(executableCriteria);
			if (firstResult >= 0) {
				executableCriteria.setFirstResult(firstResult);
			}
			if (maxResults > 0) {
				executableCriteria.setMaxResults(maxResults);
			}
			return executableCriteria.list();
		});

	}

	// -------------------------------------------------------------------------
	// Convenience finder methods for HQL strings
	// -------------------------------------------------------------------------

	@Deprecated

	public List<?> find(final String queryString, final Object... values) {
		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryString);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i, values[i]);
				}
			}
			return queryObject.list();
		});

	}

	@Deprecated

	public List<?> findByNamedParam(String queryString, String paramName, Object value) {

		return findByNamedParam(queryString, new String[] { paramName }, new Object[] { value });
	}

	@Deprecated

	public List<?> findByNamedParam(final String queryString, final String[] paramNames, final Object[] values) {

		if (paramNames.length != values.length) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryString);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			for (int i = 0; i < values.length; i++) {
				applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
			}
			return queryObject.list();
		});
	}

	@Deprecated

	public List<?> findByValueBean(final String queryString, final Object valueBean) {

		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryString);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			queryObject.setProperties(valueBean);
			return queryObject.list();
		});
	}

	// -------------------------------------------------------------------------
	// Convenience finder methods for named queries
	// -------------------------------------------------------------------------

	@Deprecated
	public List<?> findByNamedQuery(final String queryName, final Object... values) {
		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryName);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i, values[i]);
				}
			}
			return queryObject.list();
		});
	}

	@Deprecated

	public List<?> findByNamedQueryAndNamedParam(String queryName, String paramName, Object value) {

		return findByNamedQueryAndNamedParam(queryName, new String[] { paramName }, new Object[] { value });
	}

	@Deprecated

	public List<?> findByNamedQueryAndNamedParam(final String queryName, final String[] paramNames,
			final Object[] values) {

		if (values != null && (paramNames == null || paramNames.length != values.length)) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryName);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
				}
			}
			return queryObject.list();
		});
	}

	@Deprecated

	public List<?> findByNamedQueryAndValueBean(final String queryName, final Object valueBean) {

		return executeWithNativeSession((HibernateCallback<List<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryName);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			queryObject.setProperties(valueBean);
			return queryObject.list();
		});
	}

	// -------------------------------------------------------------------------
	// Convenience query methods for iteration and bulk updates/deletes
	// -------------------------------------------------------------------------

	@Deprecated

	public Iterator<?> iterate(final String queryString, final Object... values) {
		return executeWithNativeSession((HibernateCallback<Iterator<?>>) session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryString);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i, values[i]);
				}
			}
			return queryObject.iterate();
		});
	}

	@Deprecated

	public void closeIterator(Iterator<?> it) {
		try {
			Hibernate.close(it);
		} catch (HibernateException ex) {
			throw ex;
		}
	}

	@Deprecated

	public int bulkUpdate(final String queryString, final Object... values) {
		Integer result = executeWithNativeSession(session -> {
			org.hibernate.Query queryObject = null;
			try {
				queryObject = (Query) createQueryMethod.invoke(session, queryString);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
			prepareQuery(queryObject);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i, values[i]);
				}
			}
			return queryObject.executeUpdate();
		});
		return result;
	}

	/**
	 * Prepare the given Criteria object, applying cache settings and/or a
	 * transaction timeout.
	 * 
	 * @param criteria
	 *            the Criteria object to prepare
	 * @see #setCacheQueries
	 * @see #setQueryCacheRegion
	 */
	protected void prepareCriteria(Criteria criteria) {
		if (isCacheQueries()) {
			criteria.setCacheable(true);
			if (getQueryCacheRegion() != null) {
				criteria.setCacheRegion(getQueryCacheRegion());
			}
		}
		if (getFetchSize() > 0) {
			criteria.setFetchSize(getFetchSize());
		}
		if (getMaxResults() > 0) {
			criteria.setMaxResults(getMaxResults());
		}

	}

	/**
	 * Prepare the given Query object, applying cache settings and/or a transaction
	 * timeout.
	 * 
	 * @param queryObject
	 *            the Query object to prepare
	 * @see #setCacheQueries
	 * @see #setQueryCacheRegion
	 */
	protected void prepareQuery(org.hibernate.Query queryObject) {
		if (isCacheQueries()) {
			queryObject.setCacheable(true);
			if (getQueryCacheRegion() != null) {
				queryObject.setCacheRegion(getQueryCacheRegion());
			}
		}
		if (getFetchSize() > 0) {
			queryObject.setFetchSize(getFetchSize());
		}
		if (getMaxResults() > 0) {
			queryObject.setMaxResults(getMaxResults());
		}
	}

	/**
	 * Apply the given name parameter to the given Query object.
	 * 
	 * @param queryObject
	 *            the Query object
	 * @param paramName
	 *            the name of the parameter
	 * @param value
	 *            the value of the parameter
	 * @throws HibernateException
	 *             if thrown by the Query object
	 */
	@Deprecated
	protected void applyNamedParameterToQuery(org.hibernate.Query queryObject, String paramName, Object value)
			throws HibernateException {

		if (value instanceof Collection) {
			queryObject.setParameterList(paramName, (Collection<?>) value);
		} else if (value instanceof Object[]) {
			queryObject.setParameterList(paramName, (Object[]) value);
		} else {
			queryObject.setParameter(paramName, value);
		}
	}

	@Deprecated
	private static org.hibernate.Query queryObject(Object result) {
		return (org.hibernate.Query) result;
	}

	/**
	 * Invocation handler that suppresses close calls on Hibernate Sessions. Also
	 * prepares returned Query and Criteria objects.
	 * 
	 * @see Session#close
	 */
	private class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final Session target;

		public CloseSuppressingInvocationHandler(Session target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// Invocation on Session interface coming in...

			if (method.getName().equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			} else if (method.getName().equals("hashCode")) {
				// Use hashCode of Session proxy.
				return System.identityHashCode(proxy);
			} else if (method.getName().equals("close")) {
				// Handle close method: suppress, not valid.
				return null;
			}

			// Invoke method on target Session.
			try {
				Object retVal = method.invoke(this.target, args);

				// If return value is a Query or Criteria, apply transaction timeout.
				// Applies to createQuery, getNamedQuery, createCriteria.
				if (retVal instanceof Criteria) {
					prepareCriteria(((Criteria) retVal));
				} else if (retVal instanceof org.hibernate.Query) {
					prepareQuery(((org.hibernate.Query) retVal));
				}

				return retVal;
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

}
