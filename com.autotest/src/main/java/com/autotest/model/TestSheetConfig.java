package com.autotest.model;

/**
 * Region配置实体类
 * @author veaZhao
 *
 */
public class TestSheetConfig {
	String id;
	String testclass;
	String executor;

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTestclass() {
		return testclass;
	}

	public void setTestclass(String testclass) {
		this.testclass = testclass;
	}

}
