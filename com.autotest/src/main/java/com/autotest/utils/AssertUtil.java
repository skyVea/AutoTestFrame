package com.autotest.utils;

/**
 * 断言工具
 * 
 * @author veaZhao
 *
 */
public class AssertUtil {
	private AssertUtil() {

	}

	public static boolean assetBoolean(boolean... b) {
		for (int i = 0; i < b.length; i++) {
			if (b[i] == false) {
				return false;
			}
		}
		return true;
	}
}
