package com.autotest.adatper;

import java.util.List;
import java.util.Map;

import com.autotest.model.BaseTestCase;
import com.autotest.model.TestModuleConfig;
import com.autotest.model.TestResult;

/**
 * 用例适配器
 * 
 * @author veaZhao
 *
 * @param <T>
 *            测试用例对象类
 */
public abstract class TestCaseAdapter<T extends BaseTestCase> {
	protected TestModuleConfig testModuleConfig;

	public TestCaseAdapter(TestModuleConfig testModuleConfig) {
		this.testModuleConfig = testModuleConfig;
	}

	/**
	 * @return 返回用例对象集合，Map-Key表示用例集合区域,Value表示用例集合
	 * @throws Exception
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public abstract Map<String, List<T>> adaptTestCases();

	/**
	 * @param result
	 *            测试结果保存
	 */
	public abstract void saveResult(TestResult<T> result);

	/**
	 * @param region
	 *            表示用例集合区域
	 * @param testcase
	 *            一条用例
	 */
	// public abstract void saveOneCase(String regionid, T testcase);

}
