package com.si.utils;


import java.io.*;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileTool {
	/**
	 * 写文�?如果文件路径不存在，自动生成文件路径，再写文件�?
	 * @param sText				文件内容
	 * @param sFilePath			文件的目的路�?
	 * @param sTargetEncoding	按哪种编码写文件
	 * @throws Exception
	 */
	public static void WriteFile(String sText, String sFilePath, String sTargetEncoding)
		throws Exception
	{
		String dir = "";
		File fdir = null;
		if(sFilePath.indexOf("/") !=-1){
			dir = sFilePath.substring(0, sFilePath.lastIndexOf("/"));
			fdir = new File(dir);
			if(!fdir.exists())fdir.mkdirs();
		}
		if(sFilePath.indexOf("\\")!=-1){
			dir = sFilePath.substring(0, sFilePath.lastIndexOf("\\"));
			fdir = new File(dir);
			if(!fdir.exists())fdir.mkdirs();
		}
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(sFilePath, false));
			bos.write(sText.getBytes(sTargetEncoding));
			bos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			if (bos != null)
				try
				{
					bos.close();
				}
				catch (Exception exception1) { }
		}
	}

	/**
	 * 读取文件
	 * @param sFilePath			文件路径
	 * @param sTargetEncoding	按哪种编码读�?
	 * @return
	 * @throws Exception
	 */
	public static String ReadFile(String sFilePath, String sTargetEncoding)
		throws Exception
	{
		StringBuilder sText;
		BufferedReader bf;
		sText = new StringBuilder();
		bf = null;
		try {
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(sFilePath), sTargetEncoding));
			char buf[] = new char[1024];
			int len;
			while ((len = bf.read(buf)) > 0) 
				sText.append(new String(buf, 0, len));
			return sText.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (bf != null)
					bf.close();
			} catch (Exception e){}
		}
		
	}
	
	/**
	 * 根据文件绝对路径创建目录
	 * @param sFilePath
	 */
	public static void CreatSupDir(String sFilePath)
	{
		Pattern p = Pattern.compile((new StringBuilder("[/\\")).append(File.separator).append("]").toString());
		for (Matcher m = p.matcher(sFilePath); m.find();)
		{
			int index = m.start();
			String subDir = sFilePath.substring(0, index);
			File subDirFile = new File(subDir);
			if (!subDirFile.exists())
				subDirFile.mkdir();
		}

	}
	
	/**
	 * 拷贝文件
	 * @param sSource		源文�?
	 * @param sTarget		目标文件
	 * @throws Exception
	 */
	public static void CopyFile(String sSource, String sTarget)
	throws Exception
	{
		FileChannel sourceChannel;
		FileChannel targetChannel;
		sourceChannel = null;
		targetChannel = null;
		try {
			sourceChannel = (new FileInputStream(new File(sSource))).getChannel();
			targetChannel = (new FileOutputStream(new File(sTarget))).getChannel();
			targetChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
		} catch (Exception e) {
			System.out.println("copy file fail!!, \nsource file path:"+sSource+"\n"+
					"item file path:"+sTarget);
			throw e;
		} finally {
			try {
				if (targetChannel != null)
					targetChannel.close();
				if (sourceChannel != null)
					sourceChannel.close();
			} catch (Exception e) { }
		}
	}
	

	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so now it can be smoked
        return dir.delete();
    }
}
