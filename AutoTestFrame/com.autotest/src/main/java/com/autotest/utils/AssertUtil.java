package com.autotest.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.ResolutionSyntax;

import com.autotest.model.BaseTestPoint;

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

	public static boolean assertObject(Object o1, Object o2) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = o1.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			Object b1Object = fields[i].get(o1);
			Object b2Object = fields[i].get(o2);
			if (b1Object == b2Object) {
				continue;
			}
			if (!b1Object.equals(b2Object)) {
				return false;
			}
		}
		return true;
	}
}
