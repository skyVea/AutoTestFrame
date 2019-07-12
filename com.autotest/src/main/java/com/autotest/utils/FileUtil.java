package com.autotest.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * 文件操作工具
 * 
 * @author veaZhao
 *
 */
public class FileUtil {
	/**
	 * @param fis 输入流
	 * @return
	 * @throws IOException
	 */
	public static String readInputStream(FileInputStream fis) throws IOException {
		byte[] data = new byte[fis.available()];
		try {
			fis.read(data);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			fis.close();
		}
		return new String(data);
	}

	/**
	 * @param fos 输出流
	 * @param b
	 * @throws IOException
	 */
	public static void writeOutPutStream(FileOutputStream fos, byte[] b) throws IOException {
		try {
			fos.write(b);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			fos.close();
		}
	}

	/**
	 * @param pathname
	 * @param linebreak
	 *            按行读取，添加换行符如前段需要br
	 * @return
	 * @throws IOException
	 */
	public static String read(String pathname, String linebreak) throws IOException {
		BufferedReader bufferedReader = null;
		try {
			FileReader fReader = new FileReader(new File(pathname));
			bufferedReader = new BufferedReader(fReader);
			StringBuffer buffer = new StringBuffer();
			while (bufferedReader.ready()) {
				buffer.append(bufferedReader.readLine() + linebreak);
			}
			return buffer.toString();
		} finally {
			bufferedReader.close();
		}

	}
}
