package com.autotest.enumeration;

/**
 * 测试结果枚举值
 * 
 * @author veaZhao
 *
 */
public enum TestResultEnum {
	FAIL(0, "失败"), PASS(1, "通过"), BLOCK(3, "阻塞"), SKIP(4, "跳过"), NONE(5, "NONE"), ERROR(6, "错误");
	private final Integer value;
	private final String desc;

	private TestResultEnum(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public static String getDesc(Integer value) {
		TestResultEnum[] results = TestResultEnum.values();
		for (int i = 0; i < results.length; i++) {
			if (results[i].value.equals(value)) {
				return results[i].desc();
			}
		}
		return null;
	}

	public static Integer getValue(String desc) {
		TestResultEnum[] results = TestResultEnum.values();
		for (int i = 0; i < results.length; i++) {
			if (results[i].desc.equals(desc)) {
				return results[i].value();
			}
		}
		return null;
	}

	public final Integer value() {
		return value;
	}

	public final String desc() {
		return desc;
	}

}
