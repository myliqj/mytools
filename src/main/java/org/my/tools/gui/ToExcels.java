package org.my.tools.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.my.tools.excel.ExportExcel;
import org.my.tools.util.MyDbUtils;


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

	// ģ�� Ŀ¼
	static String in_path = "c:/";
	// ���� Ŀ¼
	static String out_path = "c:/";
	
	// ģ���ļ� ������������½��ļ�(����һ�б�ͷ)
	static String in_file = "tables_20190507.xlsx";
	
	// ����ļ� ��ģ��·��+�ļ�����ͬʱ �Ḳ��ģ���ļ�
	static String out_file = "tables_20190507.xlsx"; 

	static String sql_con_1 = "SET CURRENT SCHEMA = \"FSSB\"";
	static String sql_con_2 = "SET CURRENT PATH = \"SYSIBM\",\"SYSFUN\",\"SYSPROC\",\"SYSIBMADM\",\"FSSB\"";
	
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	
	public static void main(String[] args) throws Exception {
		getConn();
		// ִ�����ǰ��ִ������ģʽ���ͺ���·��
		MyDbUtils.execSql(conn, new String[]{sql_con_1,sql_con_2});
		
		try {
			
			// �򿪽����
			getRs();
			
			// ���� excel
			System.out.println("��ʼ����...");
			ExportExcel.ResultSetToExeclStartRow(rs, in_path+in_file, "Sheet1", out_path+out_file, "Sheet1", 2, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			closeAll();
		}
	}
	
	
	public static boolean getRs() throws Exception{
		stmt = conn.createStatement();
		System.out.println("run-sql:" + sql);
        rs = stmt.executeQuery(sql);
        return true;
	}
	
	public static void getConn() throws Exception{
		if (conn != null) return;
		Class.forName("com.ibm.db2.jcc.DB2Driver");
        conn = DriverManager.getConnection(url, "db2admin", "db2admin");            
	}
	
	public static void closeAll()throws Exception{
		if (rs!=null) try { rs.close();}catch(SQLException e){}
		if (stmt!=null) try { stmt.close();}catch(SQLException e){}
		if (conn!=null) try { conn.close();}catch(SQLException e){}
	}
	
	// jdbc:db2://{#ip#}:{#port#}/{#dbname#}
	static String url = "jdbc:db2://127.0.0.1:50000/test2";
	static String sql = "select * from syscat.tables";
}
