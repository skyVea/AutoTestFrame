package com.autotest.model;

/**
 * 测试结果实体类
 * 
 * @author veaZhao
 *
 * @param <T>
 */
public class TestResult<T extends BaseTestCase> {
	String moduleId;
	String regionId;
	Throwable throwable;
	T testCase;
	Integer testResult = 5;// 测试结果默认值为未执行NONE
	Long startMillis;
	Long endMillis;

	public Integer getTestResult() {
		return testResult;
	}

	public void setTestResult(Integer testResult) {
		this.testResult = testResult;
	}

	public Long getStartMillis() {
		return startMillis;
	}

	public void setStartMillis(Long startMillis) {
		this.startMillis = startMillis;
	}

	public Long getEndMillis() {
		return endMillis;
	}

	public void setEndMillis(Long endMillis) {
		this.endMillis = endMillis;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public T getTestCase() {
		return testCase;
	}

	public void setTestCase(T testCase) {
		this.testCase = testCase;
	}

}
