package org.my.tools.db.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.my.tools.util.MyStrUtils;


public class DB {
    public static String CONFIG_PREFIX = ".db";
    public static String NAME = "name";
    public static String TYPE = "type"; // db2/mssql2000/mssql2005/oracle/oracle-tns
    public static String DRIVER = "driver";
    public static String USER = "user";
    public static String PWD = "pwd";
    public static String URL_TYPE = "url_type";
    public static String URL_DBNAME = "url_dbname";
    public static String URL_IP = "url_ip";
    public static String URL_PORT = "url_port";
    public static String URL_FULL = "url_full";
    /**
     * �����б�
     */
    @SuppressWarnings("serial")
	private static Map<String,String> DRIVERS = new HashMap<String, String>(){
        {
            put("db2", "com.ibm.db2.jcc.DB2Driver");
            put("oracle", "oracle.jdbc.driver.OracleDriver");
            put("mssql2000", "com.microsoft.jdbc.sqlserver.SQLServerDriver");
            put("mssql2005", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
    };
    private String name;
    private String dbType;
    private String driver;
    private DBUrl url;
    private String user;
    private String pwd;


    /**
     * ���ݿ�����
     * @param name ���ƣ�Ψһ��ʶһ�����ݿ����� �����ƾ���������url����
     * @param dbType ���ͣ�db2/oracle �����;���������driver����
     * @param user �û���
     * @param pwd ����
     */
    public DB(String name,String dbType,String user,String pwd,DBUrl url){
        this.name=name;
        this.user=user;
        this.pwd=pwd;
        this.dbType=dbType;
        this.url= url;
        this.driver=DB.DRIVERS.get(this.dbType);
    }

    public Connection getConn() throws Exception {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url.getUrlFull(), user, pwd);
        return conn;
    }

	public static Map<String, String> getDRIVERS() {
		return DRIVERS;
	}

	public static void setDRIVERS(Map<String, String> dRIVERS) {
		DRIVERS = dRIVERS;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public DBUrl getUrl() {
		return url;
	}

	public void setUrl(DBUrl url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
    

    public static DB getDBformConfig(String configFileName,String configDBName) throws Exception {

    /* config.properties
       srcdb.db.name=fssbcs
       srcdb.db.type=db2
       srcdb.db.driver=
       srcdb.db.url_full=  ##jdbc:db2://{#ip#}:{#port#}/{#dbname#}
       srcdb.db.url_type=db2
       srcdb.db.user=fssb
       srcdb.db.pwd=pwd

       srcdb.db.url_dbname=fssbcs
       srcdb.db.url_ip=200.30.10.101
       srcdb.db.url_port=50000
       */
        Properties prop = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
        prop.load(in);
        DBUrl url = null;
        String url_full = MyStrUtils.getStringDef(prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_FULL),"");
        if(MyStrUtils.isEmpty(url_full)){
            url = new DBUrl(prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_TYPE)
                    , prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_DBNAME)
                    , prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_IP)
                    , prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_PORT));
        }else{
            url = new DBUrl(prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_TYPE)
                    ,prop.getProperty(configDBName+CONFIG_PREFIX+DB.URL_DBNAME)
                    ,url_full);
        }
        DB db = new DB(prop.getProperty(configDBName+CONFIG_PREFIX+DB.NAME)
                    , prop.getProperty(configDBName+CONFIG_PREFIX+DB.TYPE)
                    , prop.getProperty(configDBName+CONFIG_PREFIX+DB.USER)
                    , prop.getProperty(configDBName+CONFIG_PREFIX+DB.PWD)
                    , url);
        String driver = MyStrUtils.getStringDef(prop.getProperty(configDBName+CONFIG_PREFIX+DB.DRIVER),"");
        if(MyStrUtils.isNotEmpty(driver)){
            db.setDriver(driver);
        }
        return db;
//        return new DB("fssbcs", "db2", "fssb", "Fscs@0901"
//                , new DBUrl("db2", "fssbcs", "200.30.10.101", "60000"));

    }
}
