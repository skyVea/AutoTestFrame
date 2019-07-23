package com.autotest.listener;

import com.autotest.utils.StringUtils;
import com.autotest.utils.TestLog;

/**
 * 默认Module监听器，可实现监听器接口自定义监听器，也可以继承默认监听器做扩展。并通过监听器工厂注册。
 * 
 * @author veaZhao
 *
 */
public class DefaultTestModuleListener implements TestModuleListener {
	public void onStart(Object object) {
		TestLog.getInstance(this.getClass()).infoAndPersistence("==========开始执行TestModule==========");
		TestLog.getInstance(this.getClass()).infoAndPersistence(StringUtils.toString(object) + "\r\n");
	}

	public void onFinish(Object object) {
		TestLog.getInstance(this.getClass()).infoAndPersistence("==========执行完成TestModule==========" + "\r\n");
	}
}
