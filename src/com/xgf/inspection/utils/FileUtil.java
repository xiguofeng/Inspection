/*
 * Copyright (C) 2012 xbk
 * FileService.java
 * Description:
 * Author: zxt
 * Date:  2012-9-20 下午3:54:04
 */

package com.xgf.inspection.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

//得到传入的上下文对象的引用
/**
 * 保存文件
 * 
 * @param fileName 文件名
 * @param content  文件内容
 * @throws Exception
 */
// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
// Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
// Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
// Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
// MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
// 如果希望文件被其他应用读和写，可以传入：
// openFileOutput("output.txt", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
/**
 * 读取文件内容
 * 
 * @param fileName 文件名
 * @return 文件内容
 * @throws Exception
 */
// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
//将读取后的数据放置在内存中---ByteArrayOutputStream
//返回内存中存储的数据
/**
 * 文件保存与读取功能实现类
 * 
 */
public class FileUtil {

	public static final String TAG = "FileUtil";

	/**
	 * 保存文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件内容
	 * @throws Exception
	 */
	public static void save(Context context, String fileName, String content)
			throws Exception {

		// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
		// if (!fileName.endsWith(".txt")) {
		// fileName = fileName + ".txt";
		// }

		byte[] buf = fileName.getBytes("iso8859-1");

		Log.d(TAG, new String(buf, "utf-8"));

		fileName = new String(buf, "utf-8");

		Log.d(TAG, fileName);

		// Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
		// Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
		// Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
		// MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
		// 如果希望文件被其他应用读和写，可以传入：
		// openFileOutput("output.txt", Context.MODE_WORLD_READABLE +
		// Context.MODE_WORLD_WRITEABLE);

		FileOutputStream fos = context.openFileOutput(fileName,
				context.MODE_PRIVATE);
		fos.write(content.getBytes());
		fos.close();
	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件内容
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
	public static String read(Context context, String fileName) {

		// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
		// if (!fileName.endsWith(".txt")) {
		// fileName = fileName + ".txt";
		// }
		try {
			FileInputStream fis = context.openFileInput(fileName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buf = new byte[1024];
			int len = 0;

			// 将读取后的数据放置在内存中---ByteArrayOutputStream
			while ((len = fis.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}

			fis.close();
			baos.close();

			// 返回内存中存储的数据
			return baos.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return null;
		}
	}

	public static boolean exist(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

}
