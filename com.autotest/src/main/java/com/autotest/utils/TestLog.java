package com.autotest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autotest.consts.GlobalConst;

/**
 * 测试日志工具
 * 
 * @author veaZhao
 *
 */
public class TestLog {
	private static Logger logger = null;
	private static TestLog testLog = null;

	public static TestLog getInstance(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz);
		if (null == testLog) {
			testLog = new TestLog();
		}
		return testLog;
	}

	public void startTest(String content) {
		infoAndPersistence("[测试开始]" + content);
	}

	public void endTest(String content) {
		infoAndPersistence("[测试结束]" + content);
	}

	public void testStep(String content) {
		infoAndPersistence("[测试步骤]" + content);
	}

	/**
	 * 持久到本地
	 * 
	 * @param content
	 */
	public void infoAndPersistence(String content) {
		logger.info(content);
		String logpath = GlobalConst.TESTLOG + File.separator + "test.log";
		outputToFile(content + "\r\n", logpath, true);
	}

	public void info(String content) {
		logger.info(content);
	}

	public void errorAndPersistence(String content) {
		logger.info(content);
		String errlogpath = GlobalConst.TESTLOG + File.separator + "test.err.log";
		outputToFile(content, errlogpath, true);
	}

	private void outputToFile(String outputStr, String path, boolean append) {
		// 内容输出到文件
		try {
			FileOutputStream fos = new FileOutputStream(new File(path), append);
			byte[] data = outputStr.getBytes();
			FileUtil.writeOutPutStream(fos, data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
