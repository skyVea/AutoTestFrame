package com.autotest.executor;

import java.util.ArrayList;
import java.util.List;

import com.autotest.config.ConfigFactory;
import com.autotest.listener.DefaultTestExcutorListener;
import com.autotest.listener.TestExcutorListener;
import com.autotest.model.BaseTestCase;
import com.autotest.model.TestModuleConfig;
import com.autotest.model.TestResultContext;
import com.autotest.model.TestSuitConfig;
import com.autotest.report.DefaultTestReporterListener;
import com.autotest.report.TestReporterListener;
import com.autotest.utils.StringUtils;
import com.autotest.utils.TestLog;

/**
 * 测试调用入口
 * 
 * @author veaZhao
 *
 */
public class TestExecutor {
	private static TestExecutor testExecutor = null;
	private TestExcutorListener testExcutorListener = new DefaultTestExcutorListener();
	private static List<BaseTestExecutor> baseTestExecutors = new ArrayList<BaseTestExecutor>();
	private TestReporterListener<BaseTestCase> testReporterListener = new DefaultTestReporterListener();

	private TestExecutor() {
		// TODO Auto-generated constructor stub
	}

	public List<BaseTestExecutor> getBaseTestExcutor() {
		return this.baseTestExecutors;
	}

	public static TestExecutor getInstance() {
		if (testExecutor == null) {
			testExecutor = new TestExecutor();
			initBaseExcutors();
		}
		return testExecutor;
	}

	public void execute() {
		if (this.testExcutorListener != null) {
			testExcutorListener.onStart(this);
		}
		for (int i = 0; i < baseTestExecutors.size(); i++) {
			BaseTestExecutor baseTestExecutor = baseTestExecutors.get(i);
			baseTestExecutor.execute();
		}
		if (this.testExcutorListener != null) {
			testExcutorListener.onFinish(this);
		}
		if (this.testReporterListener != null) {
			testReporterListener.generateReport(TestResultContext.getInstance());
		}
	}

	private static void initBaseExcutors() {
		TestSuitConfig TestSuitConfig = ConfigFactory.getInstance().getSuitConfig();
		List<TestModuleConfig> testModuleConfigs = TestSuitConfig.getTestModuleConfigs();
		for (int i = 0; i < testModuleConfigs.size(); i++) {
			String excutorclazz = testModuleConfigs.get(i).getExecutor();
			if (!StringUtils.isEmpty(excutorclazz)) {
				BaseTestExecutor<?> executor = null;
				try {
					executor = (BaseTestExecutor<?>) Class.forName(excutorclazz).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				executor.setTestModuleConfig(testModuleConfigs.get(i));
				baseTestExecutors.add(executor);
			} else {
				try {
					TestLog.getInstance(Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()))
							.info("excutor为空：" + testModuleConfigs.get(i).getId());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
