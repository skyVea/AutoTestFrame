package com.autotest.report;

import com.autotest.listener.TestListener;
import com.autotest.model.BaseTestCase;
import com.autotest.model.TestResultContext;

public abstract interface TestReporterListener<T extends BaseTestCase> extends TestListener {
	public void generateReport(TestResultContext<T> context);
}
