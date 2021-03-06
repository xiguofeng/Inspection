package com.xgf.inspection.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.xgf.inspection.photo.utils.OSUtils;

import android.content.Context;
import android.os.Environment;

public class FileHelper {
	private Context context;
	/** SD卡是否存在 **/
	private boolean hasSD = false;
	/** SD卡的路径 **/
	private static String SDPATH;
	/** 当前程序包的路径 **/
	private String FILESPATH;
	/** 当前程序创建的路径 **/
	private static String myFilePath = "/ins/";

	public FileHelper(Context context) {
		this.context = context;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		SDPATH = Environment.getExternalStorageDirectory().getPath();
		FILESPATH = this.context.getFilesDir().getPath();
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public static File createSDFile(String fileName) throws IOException {
		File file = new File(OSUtils.getSdCardDirectory() + "/ins/" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 删除SD卡上的文件
	 * 
	 * @param fileName
	 */
	public static boolean deleteSDFile(String fileName) {
		File file = new File(OSUtils.getSdCardDirectory() + "/ins/" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 写入内容到SD卡中的txt文本中 str为内容
	 */
	public static void writeSDFile(String str, String fileName) {
		try {
			FileWriter fw = new FileWriter(OSUtils.getSdCardDirectory()
					+ "/ins/" + fileName);
			File f = new File(OSUtils.getSdCardDirectory() + "/ins/" + fileName);
			fw.write(str);
			FileOutputStream os = new FileOutputStream(f);
			DataOutputStream out = new DataOutputStream(os);
			out.writeShort(2);
			out.writeUTF("");
			System.out.println(out);
			fw.flush();
			fw.close();
			System.out.println(fw);
		} catch (Exception e) {
		}
	}

	public static void writeSDFileNew(String str, String fileName) {
		try {
			FileOutputStream fout = new FileOutputStream(
					OSUtils.getSdCardDirectory() + "/ins/" + fileName);
			byte[] bytes = str.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 读取SD卡中文本文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readSDFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		File file = new File(OSUtils.getSdCardDirectory() + "/ins/" + fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String getFILESPATH() {
		return FILESPATH;
	}

	public String getSDPATH() {
		return SDPATH;
	}

	public boolean hasSD() {
		return hasSD;
	}
}
