package org.my.tools.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.my.tools.excel.ExportExcel;
import org.my.tools.util.MyDbUtils;


/**
 * 命令行执行：
 *   cd .\mytools\target\classes
 *   java -classpath ".;../dependency/*" org.my.tools.gui.ToExcels
 *   
 * 获取 pom.xml 的 jar 文件(生成在当前目录 target 下的 dependency 目录)： 
 *   call mvn -f pom.xml dependency:copy-dependencies
 * @author liqj 2019-05-08
 *
 */
public class ToExcels {

	// 模板 目录
	static String in_path = "c:/";
	// 导出 目录
	static String out_path = "c:/";
	
	// 模板文件 如果不存在则新建文件(仅有一行表头)
	static String in_file = "单位工伤行业类别_缴费是原类_20190509.xlsx";
	
	// 输出文件 与模板路径+文件名相同时 会覆盖模板文件
	static String out_file = "单位工伤行业类别_缴费是原类_20190509.xlsx"; 

	static String sql_con_1 = "SET CURRENT SCHEMA = \"FSSB\"";
	static String sql_con_2 = "SET CURRENT PATH = \"SYSIBM\",\"SYSFUN\",\"SYSPROC\",\"SYSIBMADM\",\"FSSB\"";
	
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	
	public static void main(String[] args) throws Exception {
		getConn();
		// 执行语句前先执行设置模式名和函数路径
		MyDbUtils.execSql(conn, new String[]{sql_con_1,sql_con_2});
		
		try {
			
			// 打开结果集
			getRs();
			
			// 导出 excel
			System.out.println("开始导出...");
			ExportExcel.ResultSetToExeclStartRow(rs, in_path+in_file, "Sheet1"
					, out_path+out_file, "缴费是原类", 2
					, true, 60);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			closeAll();
		}
	}
	
	
	public static boolean getRs() throws Exception{
		stmt = conn.createStatement();
		System.out.println("run-sql:\n" + sql);
        rs = stmt.executeQuery(sql);
        return true;
	}
	
	public static void getConn() throws Exception{
		if (conn != null) return;
		Class.forName("com.ibm.db2.jcc.DB2Driver");
        conn = DriverManager.getConnection(url, "dev_liqj", "Lqj#20190201");            
	}
	
	public static void closeAll()throws Exception{
		if (rs!=null) try { rs.close();}catch(SQLException e){}
		if (stmt!=null) try { stmt.close();}catch(SQLException e){}
		if (conn!=null) try { conn.close();}catch(SQLException e){}
	}
	
	// jdbc:db2://{#ip#}:{#port#}/{#dbname#}
	static String url = "jdbc:db2://189.30.100.63:50000/fssbjdb";
	static String sql = "select sbjgdm ||'-'|| f_gg_mc('sbjg','',sbjgdm) 社保机构\n" + 
			"  ,dwbh 单位编号,dwmc 单位名称\n" + 
			"  ,dwlx||value('-'||f_gg_jdmb('dwlx',dwlx),'') 单位类型\n" + 
			"  ,gshylb||value('-'||f_gg_jdmb('gshylb',gshylb),'') \"工伤行业类别(缴费)\"\n" + 
			"  ,GSHYLB_SJFL||value('-'||f_gg_jdmb('gshylb',GSHYLB_SJFL),'') \"工伤行业类别(属性)\"\n" + 
			"from ys_dwjbxx where GSHYLB in ('001','002','003','004')\n" + 
			"  and value(dwlx,'')<>'81' --and GSHYLB_SJFL<>''\n" + 
			"order by sbjgdm ,dwbh for read only\n";
}
