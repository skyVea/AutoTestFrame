package com.autotest.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PathUtil {

	/**
	 * @param name
	 *            文件名
	 * @return 文件路径
	 */
	public static String getPath(String name) {
		java.net.URL url = PathUtil.class.getResource(name);
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

	/**
	 * @param path
	 * @return 根据物理路径获取包文件相对路径
	 */
	public static String getPKGPath(String path) {
		String rootPath = PathUtil.class.getClassLoader().getResource("").getPath();
		if (rootPath.startsWith("/")) {
			rootPath = rootPath.substring(1);
		}
		path = path.replace("\\", "/");
		path = path.replace(rootPath, "");
		return path;
	}

	// public static Set<String> getFromPkg(String pattern, String suffix) {
	// //classpath:**/vo/*.hbm.xml
	// Set<String> classes = new LinkedHashSet<String>();
	//
	// }

	/**
	 * 扫描包路径下所有的指定后缀文件
	 *
	 * @param pkg
	 *            包路径
	 * @return 文件路径
	 */
	public static Set<String> getURLFromPkg(String pkg, String suffix) {
		Set<String> pckpaths = new LinkedHashSet<>();
		String pkgDirName = pkg.replace('.', '/');
		try {
			Enumeration<URL> urls = PathUtil.class.getClassLoader().getResources(pkgDirName);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
					findPKGPathByFile(pkg, filePath, pckpaths, suffix, true);
				} else if ("jar".equals(protocol)) {// 如果是jar包文件
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					findPKGPathByJar(pkg, jar, pckpaths, suffix);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pckpaths;
	}

	/**
	 * 扫描包路径下的所有指定后缀文件
	 *
	 * @param pkgName
	 *            包名
	 * @param pkgPath
	 *            包对应的绝对地址
	 * @param classes
	 *            保存包路径下指定后缀path的集合
	 */
	private static void findPKGPathByFile(String pkgName, String pkgPath, Set<String> paths, String suffix,
			boolean scanJar) {
		File dir = new File(pkgPath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		// 过滤获取目录，or class文件
		File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith(suffix)
				|| pathname.getName().endsWith(".jar"));

		if (dirfiles == null || dirfiles.length == 0) {
			return;
		}

		for (File f : dirfiles) {
			if (f.isDirectory()) {
				findPKGPathByFile(pkgName + "." + f.getName(), pkgPath + "/" + f.getName(), paths, suffix, scanJar);
				continue;
			}
			if (f.getName().endsWith(suffix)) {
				// paths.add(pkgName.replace(".", "/") + "/" + f.getName());
				paths.add(f.getPath());
			}
			if (f.getName().endsWith(".jar")) {
				try {
					findPKGPathByJar(pkgName, new JarFile(f), paths, suffix);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 扫描jar路径下的所有指定后缀文件
	 *
	 * @param pkgName
	 *            包名
	 * @param jar
	 *            jar文件
	 * @param paths
	 *            保存包路径下指定后缀path的集合
	 */
	private static void findPKGPathByJar(String pkgName, JarFile jar, Set<String> paths, String suffix) {
		String pkgDir = pkgName.replace(".", "/");
		Enumeration<JarEntry> entry = jar.entries();
		JarEntry jarEntry;
		String name;
		while (entry.hasMoreElements()) {
			jarEntry = entry.nextElement();
			name = jarEntry.getName();
			if (name.charAt(0) == '/') {
				name = name.substring(1);
			}

			if (jarEntry.isDirectory() || !name.endsWith(suffix)) {
				// 非指定包路径， 非class文件
				continue;
			}
			// jar:http://www.example.com/ex.jar!/com/demo/Class.class
			paths.add("jar:" + jar.getName() + "!/" + name);
		}
	}

	/**
	 * 扫描包路径下所有的class文件
	 *
	 * @param pkg
	 * @return
	 */
	public static Set<Class<?>> getClassFromPkg(String pkg) {
		Set<Class<?>> classes = new LinkedHashSet<>();
		String pkgDirName = pkg.replace('.', '/');
		try {
			Enumeration<URL> urls = PathUtil.class.getClassLoader().getResources(pkgDirName);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
					findClassesByFile(pkg, filePath, classes);
				} else if ("jar".equals(protocol)) {// 如果是jar包文件
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					findClassesByJar(pkg, jar, classes);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 扫描包路径下的所有class文件
	 *
	 * @param pkgName
	 *            包名
	 * @param jar
	 *            jar文件
	 * @param classes
	 *            保存包路径下class的集合
	 */
	private static void findClassesByJar(String pkgName, JarFile jar, Set<Class<?>> classes) {
		String pkgDir = pkgName.replace(".", "/");
		Enumeration<JarEntry> entry = jar.entries();
		JarEntry jarEntry;
		String name, className;
		Class<?> claze;
		while (entry.hasMoreElements()) {
			jarEntry = entry.nextElement();
			name = jarEntry.getName();
			if (name.charAt(0) == '/') {
				name = name.substring(1);
			}

			if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
				// 非指定包路径， 非class文件
				continue;
			}

			// 去掉后面的".class", 将路径转为package格式
			className = name.substring(0, name.length() - 6);
			claze = loadClass(className.replace("/", "."));
			if (claze != null) {
				classes.add(claze);
			}
		}
	}

	/**
	 * 扫描包路径下的所有class文件
	 *
	 * @param pkgName
	 *            包名
	 * @param pkgPath
	 *            包对应的绝对地址
	 * @param classes
	 *            保存包路径下class的集合
	 */
	private static void findClassesByFile(String pkgName, String pkgPath, Set<Class<?>> classes) {
		File dir = new File(pkgPath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		// 过滤获取目录，or class文件
		File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"));

		if (dirfiles == null || dirfiles.length == 0) {
			return;
		}

		String className;
		Class clz;
		for (File f : dirfiles) {
			if (f.isDirectory()) {
				findClassesByFile(pkgName + "." + f.getName(), pkgPath + "/" + f.getName(), classes);
				continue;
			}

			// 获取类名，干掉 ".class" 后缀
			className = f.getName();
			className = className.substring(0, className.length() - 6);

			// 加载类
			clz = loadClass(pkgName + "." + className);
			if (clz != null) {
				classes.add(clz);
			}
		}
	}

	private static Class<?> loadClass(String fullClzName) {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(fullClzName);
		} catch (ClassNotFoundException e) {
			// log.error("load class error! clz: {}, e:{}", fullClzName, e);
		}
		return null;
	}

}
