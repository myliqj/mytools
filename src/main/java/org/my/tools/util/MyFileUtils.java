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
	* ������ʱ�ļ�
	* @param prefix ��ʱ�ļ���ǰ׺
	* @param suffix ��ʱ�ļ��ĺ�׺
	* @param dirName ��ʱ�ļ����ڵ�Ŀ¼���������null�������û����ĵ�Ŀ¼�´�����ʱ�ļ�
	* @return ��ʱ�ļ������ɹ����س���·�����Ĺ淶·�����ַ��������򷵻�null
	*/
	public static String createTempFile(String prefix, String suffix,
			String dirName) {
		File tempFile = null;
		try {
			if (dirName == null) {
				// ��Ĭ���ļ����´�����ʱ�ļ�
				tempFile = File.createTempFile(prefix, suffix);
				return tempFile.getCanonicalPath();
			} else {
				File dir = new File(dirName);
				// �����ʱ�ļ�����Ŀ¼�����ڣ����ȴ���
				if (!dir.exists()) {
					if (!createDir(dirName)) {
						System.out.println("������ʱ�ļ�ʧ�ܣ����ܴ�����ʱ�ļ�����Ŀ¼��");
						return null;
					}
				}
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("������ʱ�ļ�ʧ��" + e.getMessage());
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
	* ���������ļ�
	* @param destFileName �ļ���
	* @return �����ɹ�����true�����򷵻�false
	*/
	public static boolean CreateFile(String destFileName) {
		File file = new File(destFileName);

		if (file.exists()) {
			System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�Ŀ���ļ��Ѵ��ڣ�");
			return false;
		}

		if (destFileName.endsWith(File.separator)) {
			System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�Ŀ�겻����Ŀ¼��");
			return false;
		}

		if (!file.getParentFile().exists()) {
			System.out.println("Ŀ���ļ�����·�������ڣ�׼������������");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("����Ŀ¼�ļ����ڵ�Ŀ¼ʧ�ܣ�");
				return false;
			}
		}
		// ����Ŀ���ļ�
		try {
			if (file.createNewFile()) {
				System.out.println("���������ļ�" + destFileName + "�ɹ���");
				return true;
			} else {
				System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�");
			return false;
		}
	}
	/**
	* ����Ŀ¼
	* @param destDirName Ŀ��Ŀ¼��
	* @return Ŀ¼�����ɹ�����true�����򷵻�false
	*/
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("����Ŀ¼" + destDirName + "ʧ�ܣ�Ŀ��Ŀ¼�Ѵ��ڣ�");
			return false;
		}
		if (!destDirName.endsWith(File.separator))
			destDirName = destDirName + File.separator;
		// ��������Ŀ¼
		if (dir.mkdirs()) {
			System.out.println("����Ŀ¼" + destDirName + "�ɹ���");
			return true;
		} else {
			System.out.println("����Ŀ¼" + destDirName + "�ɹ���");
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
	 * �����·�������ظ�·��
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
	 * �ҵ���·���� ����·��
	 * @param fileName
	 * @return
	 */
	public static String getClassPathFileName(String fileName){
		String name = toClassRootPath(fileName);
		return MyFileUtils.class.getResource(name).getFile();
	}
	
	/**
	 * �����ļ�����·���������ڷ��ؿ��ַ���,ֻ�ڵ�ǰ·������·����
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
