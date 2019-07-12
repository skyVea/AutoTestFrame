package com.autotest.listener;

import com.autotest.enumeration.TestResultEnum;
import com.autotest.model.BaseTestCase;
import com.autotest.model.TestResult;
import com.autotest.utils.StringUtils;
import com.autotest.utils.TestLog;

/**
 * 默认用例监听器，可实现监听器接口自定义监听器，也可以继承默认监听器做扩展。并通过监听器工厂注册。
 * 
 * @author veaZhao
 *
 */
public class DefaultTestCaseListener implements TestCaseListener<BaseTestCase> {

	@Override
	public void onStart(TestResult<BaseTestCase> testResult) {
		TestLog.getInstance(this.getClass())
				.infoAndPersistence("测试用例：" + StringUtils.toString(testResult.getTestCase()));
	}

	@Override
	public void onFinish(TestResult<BaseTestCase> testResult) {
		TestLog.getInstance(this.getClass()).infoAndPersistence("执行结果：["
				+ TestResultEnum.getDesc(testResult.getTestResult()) + "]"
				+ (testResult.getThrowable() != null ? "\r\n异常信息：" + testResult.getThrowable().getMessage() : "")
				+ "\r\n");

	}

}
