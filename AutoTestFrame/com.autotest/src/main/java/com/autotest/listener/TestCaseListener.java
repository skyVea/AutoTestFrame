package com.autotest.listener;

import com.autotest.model.BaseTestCase;
import com.autotest.model.TestResult;

public abstract interface TestCaseListener<T extends BaseTestCase> extends TestListener {
	public void onStart(TestResult<T> testResult);

	public void onFinish(TestResult<T> result);
}
