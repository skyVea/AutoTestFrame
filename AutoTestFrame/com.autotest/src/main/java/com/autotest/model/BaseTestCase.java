package com.autotest.model;

/**
 * 基础测试用例实体类
 * 
 * @author veaZhao
 *
 */
public abstract class BaseTestCase {
	String code;
	Integer result;
	BaseTestPoint testPoint;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public BaseTestPoint getTestPoint() {
		return testPoint;
	}

	public void setTestPoint(BaseTestPoint testPoint) {
		this.testPoint = testPoint;
	}
}
