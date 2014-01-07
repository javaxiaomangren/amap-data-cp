package com.amap.data.save;
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.RandomAccessFile;  
import java.nio.channels.FileChannel;  
import java.nio.channels.FileLock;  

/**
 * 文件锁
 */
public class FileLocker {  
    /** 
     * get file content. 
     *  
     * @param file 
     * @return 
     */  
    public static String getFileContent(File file) {  
        String line = "";  
        String content = "";  
        try {  
            BufferedReader bf = new BufferedReader(new InputStreamReader(  
                    new FileInputStream(file)));  
            while ((line = bf.readLine()) != null) {  
                content += line;  
            }  
        } catch (FileNotFoundException e) {  
            content = "ERROR";  
        } catch (IOException e) {  
            content = "ERROR";  
        }  
        return content;  
    }  
    /** 
     * get lock. 
     *  
     * @param file 
     * @return 
     * @throws java.io.IOException
     */  
    public static FileLock getFileLock(File file) throws IOException {  
        RandomAccessFile fi = new RandomAccessFile(file, "rw");  
        FileChannel fc = fi.getChannel();  
        return fc.tryLock();  
    }  
    
    public static void main(String[] args) throws IOException {  
        File f = new File("D:/poi.txt");  
        System.out.println(getFileContent(f) + 1);// no lock  
        FileLock lock = getFileLock(f);// lock  
        System.out.println(getFileContent(f) + 2);  
        lock.release();// lock release  
        System.out.println(getFileContent(f) + 3);// no lock  
    }  
}  
