package com.autotest.model;

import java.util.List;

/**
 * Suit配置实体类
 * 
 * @author veaZhao
 *
 */
public class TestSuitConfig {
	List<TestModuleConfig> testModuleConfigs;

	public List<TestModuleConfig> getTestModuleConfigs() {
		return testModuleConfigs;
	}

	public void setTestModuleConfigs(List<TestModuleConfig> testModuleConfigs) {
		this.testModuleConfigs = testModuleConfigs;
	}
}
