package org.my.tools.gui;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.my.tools.excel.ExportExcel;
import org.my.tools.util.MyDateUtils;
import org.my.tools.util.MyDbUtils;
import org.my.tools.util.MyFileUtils;
import org.my.tools.util.MyStrUtils;


/**
 * ������ִ�У�
 *   cd .\mytools\target\classes
 *   java -classpath ".;../dependency/*" org.my.tools.gui.ToExcels
 *   
 * ��ȡ pom.xml �� jar �ļ�(�����ڵ�ǰĿ¼ target �µ� dependency Ŀ¼)�� 
 *   call mvn -f pom.xml dependency:copy-dependencies
 * @author liqj 2019-05-08
 *
 */
public class ToExcels {

	private static ToExcels toexcels = new ToExcels();
	
	// ģ�� Ŀ¼
	//static String in_path = "c:/";
	// ���� Ŀ¼
	//static String out_path = "c:/";
	
	// ģ���ļ� ������������½��ļ�(����һ�б�ͷ)
	public String in_file = "c:\\in_file.xlsx";
	public String in_sheetname = "Sheet1";
	
	// ����ļ� ��ģ��·��+�ļ�����ͬʱ �Ḳ��ģ���ļ�
	public String out_startrow = "2";
	public String out_autowidth = "true";
	public String out_autowidth_max = "60";
	public String out_file = "c:\\out_file.xlsx"; 
	public String out_sheetname = "Sheet1";

	public String connexec = "SET CURRENT SCHEMA = \"FSSB\";SET CURRENT PATH = \"SYSIBM\",\"SYSFUN\",\"SYSPROC\",\"SYSIBMADM\",\"FSSB\"";
	
	public String driver = "com.ibm.db2.jcc.DB2Driver"; 
	// jdbc:db2://{#ip#}:{#port#}/{#dbname#}
	public String url = "jdbc:db2://189.30.100.63:50000/fssbjdb";
	public String user = "dev_liqj";
	public String pwd = "pwd";
	
	public Connection conn = null;
	public Statement stmt = null;
	public ResultSet rs = null;
	
	static {
		
		try {
			toexcels.loadFormFile("/mytoexcel.properties");
		} catch (Exception e) {			
		}
		
	}
	public static ToExcels getExcels(){
		return toexcels;
	}

	public static void main(String[] args) throws Exception {
		if(args!= null && args.length>1){
			//System.out.println(Arrays.asList(args));
			if ("-f".equalsIgnoreCase(args[0])){
				toexcels.loadFormFile(args[1]);
			}
		}
		
		toexcels.showInfo();
		toexcels.OutputExcels();
	}
	public void loadFormFile(String fileName) throws Exception{
		String configFile = MyFileUtils.toClassRootPath(fileName);
		System.out.println("��ȡ�����ļ�: "+configFile);
		Properties p = MyFileUtils.getProp(configFile);
		in_file = MyStrUtils.getStringDef(p.get("in_file"),in_file);
		in_sheetname = MyStrUtils.getStringDef(p.get("in_sheetname"),in_sheetname);

		out_startrow = MyStrUtils.getStringDef(p.get("out_startrow"),out_startrow);
		out_autowidth = MyStrUtils.getStringDef(p.get("out_autowidth"),out_autowidth);
		out_autowidth_max = MyStrUtils.getStringDef(p.get("out_autowidth_max"),out_autowidth_max);
		
		out_file = MyStrUtils.getStringDef(p.get("out_file"),out_file);
		out_sheetname = MyStrUtils.getStringDef(p.get("out_sheetname"),out_sheetname);
		
		driver = MyStrUtils.getStringDef(p.get("dbinfo.driver"),driver);
		url = MyStrUtils.getStringDef(p.get("dbinfo.url"),url);
		user = MyStrUtils.getStringDef(p.get("dbinfo.user"),user);
		pwd = MyStrUtils.getStringDef(p.get("dbinfo.pwd"),pwd);
		connexec = MyStrUtils.getStringDef(p.get("dbinfo.connexec"),connexec);
		
		if(MyStrUtils.isEmpty(pwd)){
			Console con = System.console(); 
			if (con!=null){			
				System.out.print("��¼������:");
				pwd = new String(con.readPassword());
			}else{
				pwd = MyStrUtils.inputString("��¼������:");
			}
		}
		
		String sql_file = MyFileUtils.getClassPathFileName(MyStrUtils.getStringDef(p.get("sql_file"),""));
		if(MyStrUtils.isNotEmpty(sql_file)){
			// ��ָ�� sql�ļ�  �ģ����ļ����� sql���,ֻ����һ��
			System.out.println("sql-from-file:" + sql_file);
			sql = MyStrUtils.getStringDef(MyFileUtils.readFileContent(sql_file),MyStrUtils.getStringDef(p.get("sql"),sql));
		}else{
			sql = MyStrUtils.getStringDef(p.get("sql"),sql);
		}
		System.out.println("sql=" + sql);
	}
	
	public void showInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("config-info:\n")
		.append("  in_file = ").append(in_file).append("\n")
		.append("  in_sheetname = ").append(in_sheetname).append("\n")
		.append("  out_startrow = ").append(out_startrow).append("\n")
		.append("  out_autowidth = ").append(out_autowidth).append("\n")
		.append("  out_autowidth_max = ").append(out_autowidth_max).append("\n")
		.append("  out_file = ").append(out_file).append("\n")
		.append("  out_sheetname = ").append(out_sheetname).append("\n")  
		.append("  driver = ").append(driver).append("\n")
		.append("  url = ").append(url).append("\n")
		.append("  user = ").append(user).append("\n")
		.append("  pwd = ").append("******").append("\n")
		.append("  connexec = ").append(connexec).append("\n");
		
		System.out.println(sb.toString());		
	}
	public String[][] getValue(){
		return new String[][]{
			{"in_file","ģ���ļ�",in_file}
			,{"in_sheetname","ģ���ļ���ҳ��",in_sheetname}
			,{"out_startrow","���ݿ�ʼ��",out_startrow}
			,{"out_autowidth","������Զ����",out_autowidth}
			,{"out_autowidth_max","������Զ�������ֵ",out_autowidth_max}
			,{"out_file","����ļ�",out_file}
			,{"out_sheetname","����ļ���ҳ��",out_sheetname}

			,{"driver","jdbc������",driver}
			,{"url","jdbc���Ӵ�",url}
			,{"user","�û���",user}
			,{"pwd","����",pwd}
			,{"connexec","���Ӻ����е�SQL���",connexec}
			
			,{"sql","SQL���",sql}
		};
	}
	

	public void setValue(Map<String, String> values)
			throws IllegalArgumentException, IllegalAccessException {

		for (Map.Entry<String, String> entry : values.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			Field field = null;
			try {
				field = ToExcels.class.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
				throw e;
			}
			if (field != null)
				field.set(toexcels, value);

		}

	}
	
	public int OutputExcels() throws Exception {		
		getConn();
		// ִ�����ǰ��ִ������ģʽ���ͺ���·��
		MyDbUtils.execSql(conn, connexec.split(";") , true);
		
		try {
			
			// �򿪽����
			getRs();
			
			// ���� excel
			System.out.println(MyDateUtils.formatCurrentDateTime()+" ��ʼ����...");
			int rows = ExportExcel.ResultSetToExeclStartRow(rs, in_file, in_sheetname
					, out_file, out_sheetname
					, Integer.valueOf(out_startrow)
					, "true".equals(out_autowidth)
					, Integer.valueOf(out_autowidth_max));
			System.out.println(MyDateUtils.formatCurrentDateTime()+" ��������. ��������"+rows + "\n");
			return rows;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally{
			closeAll();
		}
	}
	
	
	public boolean getRs() throws Exception{
		stmt = conn.createStatement();
		System.out.println();
		System.out.println(MyDateUtils.formatCurrentDateTime()+" ��ʼִ�� sql:\n" + sql);
        rs = stmt.executeQuery(sql); 
		System.out.println(MyDateUtils.formatCurrentDateTime()+" ����ִ�� sql.\n");
        return true;
	}
	
	public void getConn() throws Exception{
		if (conn != null) return;
		Class.forName(driver);
        conn = DriverManager.getConnection(url, user , pwd);            
	}
	
	public void closeAll()throws Exception{
		if (rs!=null) try { rs.close(); rs = null;}catch(SQLException e){}
		if (stmt!=null) try { stmt.close(); stmt = null;}catch(SQLException e){}
		if (conn!=null) try { conn.close(); conn = null;}catch(SQLException e){}
	}
	
	public String sql = "select sbjgdm ||'-'|| f_gg_mc('sbjg','',sbjgdm) �籣����\n" + 
			"  ,dwbh ��λ���,dwmc ��λ����\n" + 
			"  ,dwlx||value('-'||f_gg_jdmb('dwlx',dwlx),'') ��λ����\n" + 
			"  ,gshylb||value('-'||f_gg_jdmb('gshylb',gshylb),'') \"������ҵ���(�ɷ�)\"\n" + 
			"  ,GSHYLB_SJFL||value('-'||f_gg_jdmb('gshylb',GSHYLB_SJFL),'') \"������ҵ���(����)\"\n" + 
			"from ys_dwjbxx where GSHYLB in ('001','002','003','004')\n" + 
			"  and value(dwlx,'')<>'81' --and GSHYLB_SJFL<>''\n" + 
			"order by sbjgdm ,dwbh for read only\n";
}
