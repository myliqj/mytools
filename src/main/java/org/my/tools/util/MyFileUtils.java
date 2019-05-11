package org.my.tools.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import org.my.tools.gui.ToExcels;


public class MyFileUtils {

	public static String readFile(File file, String charset) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[2048];
        int read;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((read = fis.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }

        fis.close();
        return new String(bos.toByteArray(), charset);
    }

    public static String readFile(String fileName, String charset) throws Exception {
        File file = new File(fileName);
        return readFile(file, charset);
    }
    
    public static byte[] readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[2048];
        int read;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((read = fis.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }

        fis.close();
        return bos.toByteArray();
    }

    public static byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return readFile(file);
    }
    
    public static void writeFile(String fileName, byte[] content) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(content);
        fos.close();
    }
    
    public static void writeFile(String fileName, String contentStr, String charset) throws FileNotFoundException, IOException {
        byte[] content = contentStr.getBytes(charset);
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(content);
        fos.close();
    }
    
    public static void copy(File origin, File newfile) throws FileNotFoundException, IOException {
        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }
        FileInputStream fis = new FileInputStream(origin);
        FileOutputStream fos = new FileOutputStream(newfile);
        byte[] buf = new byte[2048];
        int read;
        while ((read = fis.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }
        fis.close();
        fos.close();
    }
    
    /**
	* 创建临时文件
	* @param prefix 临时文件的前缀
	* @param suffix 临时文件的后缀
	* @param dirName 临时文件所在的目录，如果输入null，则在用户的文档目录下创建临时文件
	* @return 临时文件创建成功返回抽象路径名的规范路径名字符串，否则返回null
	*/
	public static String createTempFile(String prefix, String suffix,
			String dirName) {
		File tempFile = null;
		try {
			if (dirName == null) {
				// 在默认文件夹下创建临时文件
				tempFile = File.createTempFile(prefix, suffix);
				return tempFile.getCanonicalPath();
			} else {
				File dir = new File(dirName);
				// 如果临时文件所在目录不存在，首先创建
				if (!dir.exists()) {
					if (!createDir(dirName)) {
						System.out.println("创建临时文件失败，不能创建临时文件所在目录！");
						return null;
					}
				}
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建临时文件失败" + e.getMessage());
			return null;
		}
	}
	

	public static Vector<String> getFileLines(String file) throws Exception{
		Vector<String> items = new Vector<String>();
		String line; 		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		while ((line = br.readLine()) != null){ 
				items.add(line); 
		}
		br.close();
		return items;
	}
	public static String readFileContent(String file) throws Exception{
		StringBuilder items = new StringBuilder(); 		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line ;
		while ((line = br.readLine()) != null){ 
			items.append(line);
		}
		br.close();
		return items.toString();
	}
	/**
	* 创建单个文件
	* @param destFileName 文件名
	* @return 创建成功返回true，否则返回false
	*/
	public static boolean CreateFile(String destFileName) {
		File file = new File(destFileName);

		if (file.exists()) {
			System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;
		}

		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标不能是目录！");
			return false;
		}

		if (!file.getParentFile().exists()) {
			System.out.println("目标文件所在路径不存在，准备创建。。。");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目录文件所在的目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建单个文件" + destFileName + "失败！");
			return false;
		}
	}
	/**
	* 创建目录
	* @param destDirName 目标目录名
	* @return 目录创建成功返回true，否则返回false
	*/
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已存在！");
			return false;
		}
		if (!destDirName.endsWith(File.separator))
			destDirName = destDirName + File.separator;
		// 创建单个目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "成功！");
			return false;
		}
	}
	
	public static boolean isExists_file(String filepath)
    {
        boolean flag1 = false;
        java.io.File file = new java.io.File(filepath);
        if (file.exists())
        {
            flag1 = true;
        }
        return flag1;
    }

	public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
 
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
	public static Properties getProp(String fileName) throws IOException{
		Properties p=new Properties();
		p.load(MyFileUtils.class.getResourceAsStream(fileName));
		return p;		
	}
	
	/**
	 * 如果无路径，返回根路径
	 * @param fileName
	 * @return
	 */
	public static String toClassRootPath(String fileName){
		if (fileName == null || fileName.length()==0) return fileName;
		if (fileName!= null && !fileName.contains("/") && !fileName.contains("\\")){
			fileName = "/"+fileName;
		}
		return fileName;
	}
	
	/**
	 * 找到类路径的 绝对路径
	 * @param fileName
	 * @return
	 */
	public static String getClassPathFileName(String fileName){
		String name = toClassRootPath(fileName);
		return MyFileUtils.class.getResource(name).getFile();
	}
	
	/**
	 * 返回文件绝对路径，不存在返回空字符串,只在当前路径及类路径找
	 * @param fileName
	 * @return
	 */
	public static String findFile(String fileName){
		File f = new File(fileName);
		if(f.exists()){
			return f.getAbsolutePath();
		}else{
			f = new File(getClassPathFileName(fileName));
			if(f.exists()){
				return f.getAbsolutePath();
			}
		}
		return "";
	}
	
}
