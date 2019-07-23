package com.autotest.model;

import java.util.Date;

public class TestContext {
	Date startExcuteDate;
	Date endExcuteDate;
	public static TestContext testContext = null;

	public static TestContext getInstance() {
		if (testContext == null) {
			testContext = new TestContext();
		}
		return testContext;
	}

	public Date getStartDate() {
		return startExcuteDate;
	}

	public void setStartDate(Date startExcuteDate) {
		this.startExcuteDate = startExcuteDate;
	}

	public Date getEndDate() {
		return endExcuteDate;
	}

	public void setEndDate(Date endExcuteDate) {
		this.endExcuteDate = endExcuteDate;
	}

}
