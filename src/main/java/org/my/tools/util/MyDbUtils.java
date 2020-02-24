package org.my.tools.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.dbutils.DbUtils;

public class MyDbUtils {
	
	/**
	 * ִ����������SQL��䣨���Ƿ�Query�ࣩ
	 * @param conn ���ݿ�����
	 * @param sqls �������
	 * @param isErrorCoutinue ������ʱ�Ƿ����ִ��:true-����ִ��,false-����ͷ���
	 * @return ִ�гɹ����� 
	 * @throws Exception
	 */
	public static int execSql(Connection conn,String[] sqls,boolean isErrorCoutinue) throws Exception{
		int success = 0;
		Statement stmt = conn.createStatement();
		for (String sql : sqls) {
			if (MyStrUtils.isEmpty(sql)) continue;
			try {
				System.out.println("Execute-Sql:" + sql);
				boolean isSuccess = stmt.execute(sql);
				success++;
			} catch (Exception e) {
				e.printStackTrace();
				if (!isErrorCoutinue) break;
			}
		}
		stmt.close();
		return success;
	}

    /**
     * �ж��ֶ������Ƿ�Ϊ��ֵ����
     *
     * @param sqlType
     * @return
     */
    public static boolean isDigitalType(int sqlType) {
        switch (sqlType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
                return true;
            default:
                return false;
        }
    }
    /**
     * �ж��ֶ������Ƿ�Ϊ�ַ�����
     *
     * @param sqlType
     * @return
     */
    public static boolean isStringType(int sqlType) {
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                return true;
            default:
                return false;
        }
    }
    /**
     * �ж��ֶ������Ƿ�Ϊ����ʱ������
     *
     * @param sqlType
     * @return
     */
    public static boolean isDateTimeType(int sqlType) {
        switch (sqlType) {
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return true;
            default:
                return false;
        }
    }
    /**
     * �ж��ֶ������Ƿ���ͬ�����
     *
     * @param t1 Դ�ֶ�����
     * @param t2 Ŀ���ֶ�����
     * @param isCompatible �Ƿ�����жϼ���, true-��,false-��(Ҫ��ȫһ��,һ����ͬ���ݿ����Ҫfalse)
     * @return
     */
    public static boolean isSameJdbcType(int t1,int t2,boolean isCompatible){
        /*
		        ���� SMALLINT=5, INTEGER=4, BIGINT=-5, FLOAT=6, REAL=7, DOUBLE=8, NUMERIC=2, DECIMAL=3,
		        �ַ� CHAR=1, VARCHAR=12, LONGVARCHAR=-1, NCHAR=-15, NVARCHAR=-9, LONGNVARCHAR=-16,
		        ���� DATE=91, TIME=92, TIMESTAMP=93,
		        ������ TINYINT=-6, BIT=-7, BINARY=-2, VARBINARY=-3, LONGVARBINARY=-4, BOOLEAN=16,
		        ����� BLOB=2004, CLOB=2005, NCLOB=2011,
		        ���� NULL=0,OTHER=1111,JAVA_OBJECT=2000,DISTINCT=2001,STRUCT=2002,ARRAY=2003,REF=2006,DATALINK=70,ROWID=-8,SQLXML=2009,
        */
        if(!isCompatible) return t1==t2;
        if (isDigitalType(t1) && isDigitalType(t2)) return true;
        if (isStringType(t1) && isStringType(t2)) return true;
        if (isDateTimeType(t1) && isDateTimeType(t2)) return true;
        return t1==t2;
    }

    public static boolean isSameMetaData(ResultSetMetaData r1,ResultSetMetaData r2
            ,boolean isCompatible) throws Exception {
        if(r1.getColumnCount()!=r2.getColumnCount()) {
        	System.out.println("  �ֶ���������ͬ:" + r1.getColumnCount() + " -> "+r2.getColumnCount());
        	return false;
        }
        boolean isok = true;
        for (int i = 1; i <= r1.getColumnCount(); i++) {
        	//System.out.println(r1.getColumnName(i) + " java-type:"+r1.getColumnType(i) + " -> "+r2.getColumnType(i));
        	
            if (!r1.getColumnName(i).equals(r2.getColumnName(i))
                    && !isSameJdbcType(r1.getColumnType(i),r2.getColumnType(i),isCompatible)){
                // �ж������Ƿ���ȫ��ͬ�������Ƿ����
            	isok = false;
            	break; 
            }
        }
        if (!isok){
        	System.out.println("  �ֶ����Ͳ�����:");
        	for (int i = 1; i <= r1.getColumnCount(); i++) {
            	System.out.println("  " +r1.getColumnName(i) + " java-type:"+r1.getColumnType(i) + " -> "+r2.getColumnType(i));
        	}
        }
        
        return isok;
    }

    public static String getParamsPair(ResultSetMetaData r1) throws Exception{
        StringBuilder sb = new StringBuilder(r1.getColumnCount()*2-1);
        for (int i = 0; i < r1.getColumnCount(); i++) {
            if(i>0) sb.append(",");
            sb.append("?");
        }
        return sb.toString();
    }
    public static ResultSetMetaData getResultSetMetaData(Connection conn,String sql) throws Exception{
        return conn.createStatement().executeQuery(sql).getMetaData();
    }
    public static int[] getColumnTypes(Connection conn,String sql) throws Exception{
        ResultSetMetaData rsd = conn.createStatement().executeQuery(sql).getMetaData();
        int[] colTypes = new int[rsd.getColumnCount()];
        for(int i=0; i<=colTypes.length-1;i++){
        	//System.out.println(i + " " +rsd.getColumnName(i+1) + " " +rsd.getColumnType(i+1));
            colTypes[i] = rsd.getColumnType(i+1);
        }
        return colTypes;
    }

    public static Connection getConnection(String driverClassName, String url, String username, String password) throws Exception {
        Class.forName(driverClassName);
        return DriverManager.getConnection(url, username, password);
    }

    public static void closeQuietlys(Object ... obj){
        for(int i=0;i<obj.length;i++){
            Object o = obj[i];
            if (o == null) continue;
            if(o instanceof ResultSet){
                DbUtils.closeQuietly((ResultSet)o);
            }else if(o instanceof Statement){
                DbUtils.closeQuietly((Statement)o);
            }else if(o instanceof Connection){
                DbUtils.closeQuietly((Connection)o);
            }else{
               throw new RuntimeException("���Ͳ���ȷ��ֻ���� ResultSet,Statement,Connection");
            }
        }
    }
    

    public static String getValue(Object obj,String nullVal){
        if (obj==null) return nullVal;

        if (obj instanceof String){
            return obj.toString();
        } else if (obj instanceof java.sql.Timestamp) {
            return MyDateUtils.formatDateTime((java.sql.Timestamp) obj);
        } else if (obj instanceof java.sql.Date) {
            return MyDateUtils.formatDateTime((java.sql.Date) obj);
        } else if (obj instanceof java.sql.Time) {
            return MyDateUtils.formatDateTime((java.sql.Time) obj);
        } else if (obj instanceof oracle.sql.TIMESTAMP) {
            try { return MyDateUtils.formatDateTime(((oracle.sql.TIMESTAMP) obj).timestampValue());
            } catch (Exception e) {}
        }
        return obj.toString();
    }
    
    public static void updateOneBlob(Connection conn,String sql,byte[] data) throws SQLException{
    	PreparedStatement ps=conn.prepareStatement(sql);
        ps.setBytes(1, data);
        ps.executeUpdate();
    }

    public static boolean readOneBlobToFile(Connection conn,String sql,String fileName) throws SQLException, IOException{
    	Statement st = conn.createStatement();
    	ResultSet rs=st.executeQuery(sql);
        if (rs.next()){
        	InputStream is = rs.getBinaryStream(1);        	
        	FileOutputStream fos = new FileOutputStream(fileName); 
            byte[] buf = new byte[2048];
            int read;
            while ((read = is.read(buf)) != -1) {
                fos.write(buf, 0, read);
            } 
            fos.flush();
            fos.close();
        	is.close(); 
        	closeQuietlys(rs,st);
        	return true;
       }
       closeQuietlys(rs,st);
       return false;
    }
    
    public static byte[] readOneBlob(Connection conn,String sql) throws SQLException, IOException{
    	Statement st = conn.createStatement();
    	ResultSet rs=st.executeQuery(sql);
        if (rs.next()){
        	//Blob bb=rs.getBlob(1);
        	InputStream is = rs.getBinaryStream(1);
        	byte[] data = new byte[is.available()];
        	is.read(data);
        	is.close();
        	return data;
        	
        	//return bb.getBytes(bb.length(), 0);
//        	bb.getBinaryStream();
//        	InputStream is= bb.getBinaryStream();
//        	ObjectInputStream ois=new ObjectInputStream(is);
//        	O oo=(O) ois.readObject();
        }
        return null;
    }
    
    
}
