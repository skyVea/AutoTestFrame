package com.autotest.model;

import java.util.List;

/**
 * Module实体类
 * @author veaZhao
 *
 */
public class TestModuleConfig {
	String id;
	String path;
	String executor;
	List<TestSheetConfig> testSheetConfigs;

	public List<TestSheetConfig> getTestSheetConfigs() {
		return testSheetConfigs;
	}

	public void setTestSheetConfigs(List<TestSheetConfig> testSheetConfigs) {
		this.testSheetConfigs = testSheetConfigs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

}
