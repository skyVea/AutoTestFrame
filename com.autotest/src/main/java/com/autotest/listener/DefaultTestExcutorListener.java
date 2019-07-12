package com.autotest.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.autotest.utils.TestLog;

/**
 * 默认测试执行器监听器，可实现监听器接口自定义监听器，也可以继承默认监听器做扩展。并通过监听器工厂注册。
 * 
 * @author veaZhao
 *
 */
public class DefaultTestExcutorListener implements TestExcutorListener {
	public void onStart(Object object) {
		TestLog.getInstance(this.getClass()).infoAndPersistence(
				"[AutoTest]开始执行...\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\r\n");
	}

	public void onFinish(Object object) {
		TestLog.getInstance(this.getClass()).infoAndPersistence("[AutoTest]执行完成!");
	}
}
