package com.autotest.utils;

import java.io.UnsupportedEncodingException;

public class PathUtil {

	public static String getPath(String name) {
		String path = PathUtil.class.getResource(name).getPath();
		try {
			path = java.net.URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return path;
	}

}
