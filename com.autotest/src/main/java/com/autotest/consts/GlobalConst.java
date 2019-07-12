package com.autotest.consts;

import java.io.File;

/**
 * 全局常量
 * 
 * @author veaZhao
 *
 */
public class GlobalConst {
	// test_home 可以改为环境变量使用
	public static final String TEST_HOME = System.getenv().get("TEST_HOME");
	// TESTSUIT
	public final static String TESTSUIT = TEST_HOME + File.separator + "global" + File.separator + "TestSuit.xml";
	// 日志持久化路径
	public final static String TESTLOG = TEST_HOME + File.separator + "log";

	// 测试报告路径
	public final static String TESTREPORT = TEST_HOME;

}
