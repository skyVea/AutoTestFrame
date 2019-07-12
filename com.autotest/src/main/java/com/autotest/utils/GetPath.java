package com.autotest.utils;

import java.io.File;
import java.net.URL;

/**
 * 路径获取工具
 * 
 * @author veaZhao
 *
 */
public class GetPath {
	// 获取WebRoot目录
	public static String getWebRootPath() {
		URL urlpath = GetPath.class.getResource("");
		String path = urlpath.toString();
		if (path.startsWith("file")) {
			path = path.substring(5);
		}
		if (path.indexOf("WEB-INF") > 0) {
			path = path.substring(0, path.indexOf("WEB-INF") - 1);
		}
		path.replace("/", File.separator);
		return path;
	}

	// webroot WebRoot目录
	// filename 文件名
	// ...args 文件名所在文件夹，多个参数输入
	public static String getWebRootFilepath(String webroot, String filename, String... args) {
		String pre = webroot;
		String path = pre;
		for (String arg : args) {
			path += File.separator + arg;
		}
		path += File.separator + filename;
		if (path.startsWith("file")) {
			path = path.substring(5);
		}
		path.replace("/", File.separator);
		return path;
	}
}
