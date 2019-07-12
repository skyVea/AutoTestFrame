package com.autotest.executor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.autotest.adatper.TestCaseAdapter;
import com.autotest.enumeration.TestResultEnum;
import com.autotest.listener.DefaultTestCaseListener;
import com.autotest.listener.DefaultTestModuleListener;
import com.autotest.listener.DefaultTestRegionListener;
import com.autotest.listener.TestCaseListener;
import com.autotest.listener.TestModuleListener;
import com.autotest.listener.TestRegionListener;
import com.autotest.model.BaseTestCase;
import com.autotest.model.TestModuleConfig;
import com.autotest.model.TestResult;
import com.autotest.model.TestResultContext;
import com.autotest.model.TestSheetConfig;
import com.autotest.utils.StringUtils;
import com.autotest.utils.TestLog;

/**
 * 用例执行器
 * 
 * @author veaZhao
 *
 * @param <T>
 */
public abstract class BaseTestExecutor<T extends BaseTestCase> {
	public TestModuleConfig testModuleConfig;
	private TestRegionListener testRegionListener = new DefaultTestRegionListener();
	private TestCaseListener testCaseListener = new DefaultTestCaseListener();
	private TestModuleListener testModuleListener = new DefaultTestModuleListener();
	// private TestReporterListener<BaseTestCase> testReporterListener = new
	// DefaultTestReporterListener();

	/**
	 * @return 获取TestSuit中TestModule
	 */
	public TestModuleConfig getTestModuleConfig() {
		return testModuleConfig;
	}

	public void setTestModuleConfig(TestModuleConfig testModuleConfig) {
		this.testModuleConfig = testModuleConfig;
	}

	public void execute() {
		if (this.testModuleListener != null) {
			testModuleListener.onStart(this.testModuleConfig);
		}
		Map<String, List<T>> testcases = getTestCaseAdapter().adaptTestCases();
		if (testcases == null || testcases.isEmpty()) {
			throw new RuntimeException(StringUtils.toString(this.testModuleConfig) + "获取到的用例数为空");
		}
		List<TestSheetConfig> testSheetConfigs = this.testModuleConfig.getTestSheetConfigs();
		if (testSheetConfigs == null || testcases.isEmpty()) {
			throw new RuntimeException(StringUtils.toString(this.testModuleConfig) + "TestSuit.xml没有配置Region");
		}
		for (int i = 0; i < testSheetConfigs.size(); i++) {
			TestSheetConfig testSheetConfig = testSheetConfigs.get(i);
			List<T> cases = testcases.get(testSheetConfig.getId());
			if (testRegionListener != null) {
				testRegionListener.onStart(testSheetConfig);
			}
			if (cases == null) {
				TestLog.getInstance(this.getClass())
						.infoAndPersistence(StringUtils.toString(testSheetConfig) + "用例集合为空");
			} else {
				for (int j = 0; j < cases.size(); j++) {
					TestResult<T> testResult = new TestResult<T>();
					testResult.setTestCase(cases.get(j));
					testResult.setModuleId(this.testModuleConfig.getId());
					testResult.setRegionId(testSheetConfig.getId());
					testResult.setStartMillis(new Date().getTime());
					if (testCaseListener != null) {
						testCaseListener.onStart(testResult);
					}
					T resultCase = null;
					try {
						resultCase = execute(testSheetConfig.getId(), cases.get(j));
						if (resultCase != null) {
							testResult.setTestResult(resultCase.getResult());
						} else {
							throw new RuntimeException("测试用例方法返回的对象为空，不能保存测试结果！请检查测试代码返回值！");
						}
					} catch (Exception e) {
						testResult.setTestResult(TestResultEnum.ERROR.value());
						testResult.setThrowable(e);
					}
					if (testCaseListener != null) {
						testCaseListener.onFinish(testResult);
					}
					testResult.setEndMillis(new Date().getTime());
					getTestCaseAdapter().saveResult(testResult);// 保存测试结果
					TestResultContext<T> testResultContext = TestResultContext.getInstance();
					testResultContext.addTestResult(testResult);
				}
			}
			if (testRegionListener != null) {
				testRegionListener.onFinish(testSheetConfig);
			}
		}
		if (this.testModuleListener != null) {
			testModuleListener.onFinish(null);
		}
		// if (this.testReporterListener != null) {
		// testReporterListener.generateReport(TestResultContext.getInstance());
		// }
	}

	/**
	 * @return 根据用例适配器获取用例集合
	 */
	protected abstract TestCaseAdapter<T> getTestCaseAdapter();

	/**
	 * 执行测试
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	protected abstract T execute(String region, T testcase) throws Exception;

}
