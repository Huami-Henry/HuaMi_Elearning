package com.huami.elearning.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huami.elearning.model.CompressStatus;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

public class ZipUtil{
	/**
	 * 解压文件（压缩包中不能出现中文）
	 * @param zipFileName
	 * @param targetBaseDirName
	 */
	public static void upzipFile(File file, String targetBaseDirName,Handler handler,boolean del){
		Log.e("我的临时模板", "解压模板");
		if (!targetBaseDirName.endsWith(File.separator)){
			targetBaseDirName += File.separator;
		}
		try {
			//根据ZIP文件创建ZipFile对象
			java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file);
			ZipEntry entry = null;
			String entryName = null;
			String targetFileName = null;
			byte[] buffer = new byte[4096];
			int bytes_read;
			//获取ZIP文件里所有的entry
			Enumeration entrys = zipFile.entries();
			//遍历所有entry
			while (entrys.hasMoreElements()) {
				entry = (ZipEntry)entrys.nextElement();
				//获得entry的名字
				entryName =  entry.getName();
				targetFileName = targetBaseDirName + entryName;

				if (entry.isDirectory()){
					//  如果entry是一个目录，则创建目录
					new File(targetFileName).mkdirs();
					continue;
				} else {
					//	如果entry是一个文件，则创建父目录
					new File(targetFileName).getParentFile().mkdirs();
				}
				//否则创建文件
				File targetFile = new File(targetFileName);
				//打开文件输出流
				FileOutputStream os = new FileOutputStream(targetFile);
				//从ZipFile对象中打开entry的输入流
				InputStream is = zipFile.getInputStream(entry);
				while ((bytes_read = is.read(buffer)) != -1){
					os.write(buffer, 0, bytes_read);
				}
				//关闭流
				os.close( );
				is.close( );
				Log.e("我的临时模板", "解压模板成功");
			}
			if (del) {
				file.delete();
			}
			handler.sendEmptyMessage(CompressStatus.COMPLETED);
		} catch (IOException err) {
			handler.sendEmptyMessage(CompressStatus.ERROR);
		}
	}

	private static final String password = "d2x1z0s6";//需要重新配置

	public static void unZipFileWithProgress(final File zipFile, final String filePath, final Handler handler,
											 final boolean isDeleteZip) throws ZipException
	{
		ZipFile zFile = new ZipFile(zipFile);
		zFile.setFileNameCharset("GBK");

		if (!zFile.isValidZipFile())
		{ // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
			throw new ZipException("压缩文件不合法,可能被损坏.");
		}
		File destDir = new File(filePath); // 解压目录
		if (!destDir.exists()) {
			destDir.mkdir();
		} else {
			deleteDirectory(filePath);
			destDir.mkdirs();
		}
		if (zFile.isEncrypted())
		{
			zFile.setPassword(password); // 设置密码
		}

		final ProgressMonitor progressMonitor = zFile.getProgressMonitor();
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Bundle bundle = null;
				Message msg = null;
				try
				{
					int precentDone = 0;
					if (handler == null)
					{
						return;
					}
					handler.sendEmptyMessage(CompressStatus.START);
					while (true)
					{
						// 启动线程，每隔一秒向外handler一条消息
						Thread.sleep(5);
						precentDone = progressMonitor.getPercentDone();
						bundle = new Bundle();
						bundle.putInt(CompressStatus.PERCENT, precentDone);
						msg = new Message();
						msg.what = CompressStatus.HANDLING;
						msg.setData(bundle);
						handler.sendMessage(msg);
						if (precentDone >= 100)
						{
							break;
						}
					}
					handler.sendEmptyMessage(CompressStatus.COMPLETED);
				}
				catch (InterruptedException e)
				{
					bundle = new Bundle();
					bundle.putString(CompressStatus.ERROR_COM, e.getMessage());
					msg = new Message();
					msg.what = CompressStatus.ERROR;
					msg.setData(bundle);
					handler.sendMessage(msg);
					e.printStackTrace();
				}
				finally
				{
					if (isDeleteZip)
					{
						zipFile.delete();
					}
				}
			}
		});
		thread.start();
		zFile.setRunInThread(true);
		zFile.extractAll(filePath); // 解压到此文件夹中
	}
	/**
	 * 删除文件夹以及目录下的文件
	 * @param   filePath 被删除目录的文件路径
	 * @return  目录删除成功返回true，否则返回false
	 */
	public  static boolean deleteDirectory(String filePath) {
		//如果filePath不以文件分隔符结尾，自动添加文件分隔符
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		File[] files = dirFile.listFiles();
		//遍历删除文件夹下的所有文件(包括子目录)
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				//删除子文件
				deleteFile(files[i].getAbsolutePath());
			} else {
				//删除子目录
				deleteDirectory(files[i].getAbsolutePath());
			}
		}
		//删除当前空目录
		return dirFile.delete();
	}
	/**
	 * 删除单个文件
	 * @param   filePath    被删除文件的文件名
	 * @return 文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}
}
