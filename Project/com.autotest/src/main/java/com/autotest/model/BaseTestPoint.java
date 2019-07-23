package com.autotest.model;

import java.util.Map;

/**
 * 基础测试点实体类
 * 
 * @author veaZhao
 *
 */
public class BaseTestPoint {
	String code;
	Map params;

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
