package com.autotest.model;

import java.util.Properties;

public class DataSourceConfig {
	private String id = null;
	private String ref = null;
	private String defTM = null;
	private String driverClassName = null;
	private String url = null;
	private String user = null;
	private String password = null;
	private String DSType = "HIK";
	private String usedJNDIName;
	private Properties poolProperties = new Properties();
	private String dbType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getDefTM() {
		return defTM;
	}

	public void setDefTM(String defTM) {
		this.defTM = defTM;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDSType() {
		return DSType;
	}

	public void setDSType(String dSType) {
		DSType = dSType;
	}

	public String getUsedJNDIName() {
		return usedJNDIName;
	}

	public void setUsedJNDIName(String usedJNDIName) {
		this.usedJNDIName = usedJNDIName;
	}

	public Properties getPoolProperties() {
		return poolProperties;
	}

	public void setPoolProperties(Properties poolProperties) {
		this.poolProperties = poolProperties;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

}
