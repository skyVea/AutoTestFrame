package com.autotest.listener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autotest.utils.FieldUtil;

/**
 * 监听器工厂，可以注册自定义监听器
 * 
 * @author veaZhao
 *
 */
public class TestListenerFactory {
	private static Map<String, Object> LISTENERCACHE = new HashMap<String, Object>();

	/**
	 * @param object
	 *            被注册的宿主
	 * @param testListener
	 *            注册的监听器
	 */
	public static void registerTestListener(Object object, TestListener testListener) {
		if (LISTENERCACHE.containsKey(testListener.getClass().getName())) {
			return;
		}
		List<Field> allfields = FieldUtil.getAllFields(object.getClass());
		for (int i = 0; i < allfields.size(); i++) {
			for (int j = 0; j < testListener.getClass().getInterfaces().length; j++) {
				if (allfields.get(i).getType() == testListener.getClass().getInterfaces()[j]) {
					try {
						allfields.get(i).setAccessible(true);
						allfields.get(i).set(object, testListener);
						LISTENERCACHE.put(testListener.getClass().getName(), testListener);
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!LISTENERCACHE.containsKey(testListener.getClass().getName())) {
			throw new RuntimeException(
					"对象[" + object.getClass().getName() + "]注册监听器[" + testListener.getClass().getName() + "]失败!");
		}
	}

}
