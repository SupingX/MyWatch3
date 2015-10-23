package com.mycj.mywatch.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileUtil {
	 private static final  String TAG = "FileUtil";  
//	    private static final File parentPath = Environment.getExternalStorageDirectory();  
	    private static final File parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
	    private static   String storagePath = "";  
	    private static final String DST_FOLDER_NAME = "MyWatch";  
	  
	    
	    
	    

		/**
		 * 获取SDCard的目录路径功能
		 * 
		 * @return
		 */
		private static String getSDCardPath() {
			File sdcardDir = null;
			// 判断SDCard是否存在
			boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			if (sdcardExist) {
//				sdcardDir = parentPath;
				sdcardDir = Environment.getExternalStorageDirectory();  
			}else{
			
			}
			return sdcardDir.toString();
		}

		/**
		 * 获取和保存当前屏幕的截图
		 */
		public static  String getandSaveCurrentImage(Activity ac,Bitmap Bmp) {
			String filepath ="";
			// 图片存储路径
			String savePath = getSDCardPath() + "/"+DST_FOLDER_NAME+"/ScreenImages";
			
			if (savePath==null) {
				Toast.makeText(ac, "Sd卡不可用", Toast.LENGTH_SHORT).show();
				return null;
			}
			// 保存Bitmap
			try {
				File path = new File(savePath);
				// 文件
				 filepath = savePath + "/Screen_1.png";
				File file = new File(filepath);
				if (!path.exists()) {
					path.mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fos = null;
				fos = new FileOutputStream(file);
				if (null != fos) {
					Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
					fos.flush();
					fos.close();
//					Toast.makeText(ac, "分享文件已保存至"+savePath+"目录下", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return filepath;
		}
	    
	    /**初始化保存路径 
	     * @return 
	     */  
	    private static String initPath(){  
	    	if (storagePath.equals("")) {
				storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
				File f = new File(storagePath);
				if (!f.exists()) {
					f.mkdir();
				}
			}
			return storagePath;
	    }  
	  
	    /**保存Bitmap到sdcard 
	     * @param b 
	     */  
	    public static void saveBitmap(Bitmap b){  
			String path = initPath();
			long dataTake = System.currentTimeMillis();
			String jpegName = path + "/" + dataTake + ".jpg";
			Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
			try {
				FileOutputStream fout = new FileOutputStream(jpegName);
				BufferedOutputStream bos = new BufferedOutputStream(fout);
				b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
				Log.i(TAG, "saveBitmap成功");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "saveBitmap:失败");
				e.printStackTrace();
			}
	    }  
}
