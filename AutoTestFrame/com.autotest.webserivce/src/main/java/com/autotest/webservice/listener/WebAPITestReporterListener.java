package com.autotest.webservice.listener;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.autotest.consts.GlobalConst;
import com.autotest.enumeration.TestResultEnum;
import com.autotest.model.TestResult;
import com.autotest.model.TestResultContext;
import com.autotest.report.TestReporterListener;
import com.autotest.utils.FileUtil;
import com.autotest.utils.StringUtils;
import com.autotest.webserivce.model.WebAPITestCase;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ResourceCDN;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * WebAPI测试报告监听器
 * @author veaZhao
 *
 */
public class WebAPITestReporterListener implements TestReporterListener<WebAPITestCase> {

	private static final String FILE_NAME = "Extent.html";

	private ExtentReports extent;

	private void init() {
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(GlobalConst.TESTREPORT + File.separator + FILE_NAME);
		htmlReporter.config().setDocumentTitle("WebServiceAPI自动化测试报告");
		htmlReporter.config().setReportName("WebServiceAPI自动化测试报告");
		htmlReporter.config().setEncoding("UTF-8");
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
		htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
//		htmlReporter.setAppendExisting(false);
		extent = new ExtentReports();
//		extent.setAnalysisStrategy(AnalysisStrategy.TEST);
		extent.attachReporter(htmlReporter);
		extent.setReportUsesManualConfiguration(true);
	}

	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	@Override
	public void generateReport(TestResultContext<WebAPITestCase> context) {
		init();
		List<TestResult<WebAPITestCase>> results = context.getTestResults();
		try {
			String content = FileUtil.read(GlobalConst.TESTLOG + File.separator + "test.log", "<br>");
			extent.setTestRunnerOutput(content);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ExtentTest test;
		for (int i = 0; i < results.size(); i++) {
			TestResult<WebAPITestCase> result = results.get(i);
			test = extent.createTest(result.getTestCase().getCode() + "_" + result.getTestCase().getName()); // 显示方法名称
			// test.createNode("子案例" + i); // 创建子案例
			test.assignCategory(result.getModuleId(),result.getRegionId()); // Region
			if (result.getTestResult() == TestResultEnum.PASS.value()) {
				test.pass("测试通过");
			} else if (result.getTestResult() == TestResultEnum.FAIL.value()) {
				test.fail("测试失败");
			} else if (result.getTestResult() == TestResultEnum.ERROR.value()) {
				test.error("ERROR错误");
			} else if (result.getTestResult() == TestResultEnum.SKIP.value()) {
				test.skip("跳过");
			}
			if (result.getThrowable() != null) {
				test.log(Status.ERROR, result.getThrowable()); // 异常案例，显示log到报告
			}
			test.getModel().setStartTime(getTime(result.getStartMillis()));
			test.getModel().setEndTime(getTime(result.getEndMillis()));
			test.getModel().setDescription(StringUtils.toString(result.getTestCase()));
		}
		extent.flush();
	}

}