package com.lazyeraser.imas.cgss.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileHelper {

	public static boolean writeFile(byte[] file, String filePath, String fileName) {
			
		FileOutputStream fos = null;
	
		try {
			File destDir = new File(filePath);
			if (!destDir.exists()) {
				if (!destDir.mkdir()){
					Utils.mPrint("目录创建失败，请检查文件夹权限等问题");
					return false;
				}
			}
			fos = new FileOutputStream(new File(destDir, fileName));
			fos.write(file);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		Utils.mPrint("写入文件成功" + filePath + "/" + fileName);
		return true;
	}
}
