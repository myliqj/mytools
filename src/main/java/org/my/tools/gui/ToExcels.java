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

	private static ToExcels toexcels = new ToExcels();
	
	// 模板 目录
	//static String in_path = "c:/";
	// 导出 目录
	//static String out_path = "c:/";
	
	// 模板文件 如果不存在则新建文件(仅有一行表头)
	public String in_file = "c:\\in_file.xlsx";
	public String in_sheetname = "Sheet1";
	
	// 输出文件 与模板路径+文件名相同时 会覆盖模板文件
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
		System.out.println("读取配置文件: "+configFile);
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
				System.out.print("请录入密码:");
				pwd = new String(con.readPassword());
			}else{
				pwd = MyStrUtils.inputString("请录入密码:");
			}
		}
		
		String sql_file = MyFileUtils.getClassPathFileName(MyStrUtils.getStringDef(p.get("sql_file"),""));
		if(MyStrUtils.isNotEmpty(sql_file)){
			// 有指定 sql文件  的，从文件加载 sql语句,只当作一句
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
			{"in_file","模板文件",in_file}
			,{"in_sheetname","模板文件的页名",in_sheetname}
			,{"out_startrow","数据开始行",out_startrow}
			,{"out_autowidth","输出列自动宽度",out_autowidth}
			,{"out_autowidth_max","输出列自动宽度最大值",out_autowidth_max}
			,{"out_file","输出文件",out_file}
			,{"out_sheetname","输出文件的页名",out_sheetname}

			,{"driver","jdbc驱动类",driver}
			,{"url","jdbc联接串",url}
			,{"user","用户名",user}
			,{"pwd","密码",pwd}
			,{"connexec","联接后运行的SQL语句",connexec}
			
			,{"sql","SQL语句",sql}
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
		// 执行语句前先执行设置模式名和函数路径
		MyDbUtils.execSql(conn, connexec.split(";") , true);
		
		try {
			
			// 打开结果集
			getRs();
			
			// 导出 excel
			System.out.println(MyDateUtils.formatCurrentDateTime()+" 开始导出...");
			int rows = ExportExcel.ResultSetToExeclStartRow(rs, in_file, in_sheetname
					, out_file, out_sheetname
					, Integer.valueOf(out_startrow)
					, "true".equals(out_autowidth)
					, Integer.valueOf(out_autowidth_max));
			System.out.println(MyDateUtils.formatCurrentDateTime()+" 结束导出. 总行数："+rows + "\n");
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
		System.out.println(MyDateUtils.formatCurrentDateTime()+" 开始执行 sql:\n" + sql);
        rs = stmt.executeQuery(sql); 
		System.out.println(MyDateUtils.formatCurrentDateTime()+" 结束执行 sql.\n");
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
	
	public String sql = "select sbjgdm ||'-'|| f_gg_mc('sbjg','',sbjgdm) 社保机构\n" + 
			"  ,dwbh 单位编号,dwmc 单位名称\n" + 
			"  ,dwlx||value('-'||f_gg_jdmb('dwlx',dwlx),'') 单位类型\n" + 
			"  ,gshylb||value('-'||f_gg_jdmb('gshylb',gshylb),'') \"工伤行业类别(缴费)\"\n" + 
			"  ,GSHYLB_SJFL||value('-'||f_gg_jdmb('gshylb',GSHYLB_SJFL),'') \"工伤行业类别(属性)\"\n" + 
			"from ys_dwjbxx where GSHYLB in ('001','002','003','004')\n" + 
			"  and value(dwlx,'')<>'81' --and GSHYLB_SJFL<>''\n" + 
			"order by sbjgdm ,dwbh for read only\n";
}
