package com.autotest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试结果上下文
 * 
 * @author veaZhao
 *
 * @param <T>
 */
public class TestResultContext<T extends BaseTestCase> {
	private static TestResultContext testResultContext = null;
	private Map<String, Map<String, List<T>>> testResultMap = new HashMap<String, Map<String, List<T>>>();
	private List<TestResult<T>> testResults = new ArrayList<TestResult<T>>();

	public static TestResultContext getInstance() {
		if (testResultContext == null) {
			testResultContext = new TestResultContext();
		}
		return testResultContext;
	}

	private synchronized void addTestResult0(String moduleId, String regionId, T testCase) {
		if (!testResultMap.containsKey(moduleId)) {
			Map<String, List<T>> regionMap = new HashMap<String, List<T>>();
			List<T> cases = new ArrayList<T>();
			regionMap.put(regionId, cases);
			testResultMap.put(moduleId, regionMap);
		} else {
			if (!testResultMap.get(moduleId).containsKey(regionId)) {
				List<T> cases = new ArrayList<T>();
				testResultMap.get(moduleId).put(regionId, cases);
			}
		}
		testResultMap.get(moduleId).get(regionId).add(testCase);
	}

	public synchronized void addTestResult(TestResult<T> testResult) {
		testResults.add(testResult);
	}

	public List<TestResult<T>> getTestResults() {
		return this.testResults;
	}

}