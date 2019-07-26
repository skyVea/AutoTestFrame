package com.autotest.utils;

import java.io.UnsupportedEncodingException;

public class PathUtil {

	public static String getPath(String name) {
		java.net.URL url =PathUtil.class.getResource(name);
		if (url == null) {
			return null;
		}
		String path = url.getPath();
		try {
			path = java.net.URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return path;
	}

}
