package com.autotest.consts;

import java.io.File;

import com.autotest.utils.PathUtil;

/**
 * 全局常量
 * 
 * @author veaZhao
 *
 */
public class GlobalConst {
	// test_home 可以改为环境变量使用
	public static final String TEST_HOME = System.getenv().get("TEST_HOME");
	// GLOBAL
	public final static String TESTGLOBAL = TEST_HOME + File.separator + "global";
	// 数据源配置文件
	public final static String DSCONFIG = TEST_HOME + File.separator + "global" + File.separator + "datasources.xml";
	// TESTSUIT
	public final static String TESTSUIT = PathUtil.getPath("/TestSuit.xml");
	// 日志持久化路径
	public final static String TESTLOG = TEST_HOME + File.separator + "log";
	// 测试结果Excel路径
	public final static String TESTRESULT_EXCEL = TEST_HOME + File.separator + "excel";
	// 测试报告路径
	public final static String TESTREPORT = TEST_HOME;

}
