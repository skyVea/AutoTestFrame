package com.autotest.webserivce.model;

import com.autotest.annotation.Column;
import com.autotest.enumeration.TestResultEnum;
import com.autotest.model.BaseTestCase;
import com.autotest.model.BaseTestPoint;

/**
 * WebAPI测试用例实体类
 * 
 * @author veaZhao
 *
 */
public class WebAPITestCase extends BaseTestCase {
	@Column(columName = "用例编码", isPrimary = true, isIncrement = true)
	String code;
	@Column(columName = "功能模块")
	String module;
	@Column(columName = "接口方法", isRequired = true)
	String name;
	@Column(columName = "描述")
	String detail;
	@Column(columName = "优先级")
	String priority;
	@Column(columName = "测试目的")
	String order;
	@Column(columName = "参数", isRequired = true)
	BaseTestPoint testPoint;
	@Column(columName = "期望结果", isRequired = true)
	String expected;
	@Column(columName = "测试结果", isReadOnly = false)
	Integer result;
	@Column(columName = "备注")
	String comment;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BaseTestPoint getTestPoint() {
		return testPoint;
	}

	public void setTestPoint(BaseTestPoint testPoint) {
		this.testPoint = testPoint;
	}

	public static void main(String[] args) {
		System.out.println(TestResultEnum.PASS.value() + TestResultEnum.PASS.name() + TestResultEnum.PASS.desc());
	}

}
