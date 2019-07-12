package com.autotest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ObjectMapper
 * @author veaZhao
 *
 */
public class JsonUtils {
	static ObjectMapper objectMapper;

	// 解析json
	public static <T> T fromJson(String content, Class<T> valuetype) {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		try {
			return objectMapper.readValue(content, valuetype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 生成json
	public static String toJson(Object object) {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
