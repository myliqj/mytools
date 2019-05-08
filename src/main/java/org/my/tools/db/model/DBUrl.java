package org.my.tools.db.model;

import java.util.HashMap;
import java.util.Map;

public class DBUrl {
    /**
     * jdbc ���Ӵ��б�,ʹ��  {##} ռλ������ip/port/dbname
     */
    @SuppressWarnings("serial")
	private static Map<String,String> JDBC_URL = new HashMap<String, String>(){
        {
            put("db2", "jdbc:db2://{#ip#}:{#port#}/{#dbname#}");
            put("oracle", "jdbc:oracle:thin:@{#ip#}:{#port#}:{#dbname#}");
            put("oracle-tns","jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS=(PROTOCOL=TCP)(HOST={#ip#})(PORT={#port#})))(SOURCE_ROUTE = off)(FAILOVER = on)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME={#dbname#})))");
            put("mssql2000","jdbc:microsoft:sqlserver://{#ip#}:{#port#};DataBaseName={#dbname#}");
            put("mssql2005","jdbc:sqlserver://{#ip#}:{#port#};DataBaseName={#dbname#}");
        }
    };

    private String urlDbType;
    private String urlFull;
    private String urlIp;
    private String urlPort;
    private String urlDbName;
    /**
     * Ψһȷ��һ�����ݿ⣬����IP+DbName
     * @param urlDbType jdbc���ݿ�����:db2/oracle ��ȷ���������� ,ָ url_db_type
     * @param urlDbName ���ݿ�����
     * @param urlIp IP��ַ
     * @param urlPort �˿�
     */
    public DBUrl(String urlDbType,String urlDbName,String urlIp,String urlPort){
        this.urlDbType=urlDbType;
        this.urlDbName=urlDbName;this.urlIp=urlIp; this.urlPort=urlPort;
        this.urlFull = JDBC_URL.get(this.urlDbType).replace("{#dbname#}", this.urlDbName)
                .replace("{#ip#}", this.urlIp).replace("{#port#}", this.urlPort);
    }
    public DBUrl(String urlDbType,String urlDbName,String urlFull){
        this.urlDbType=urlDbType;
        this.urlDbName=urlDbName;
        this.urlFull = urlFull;
    }

    public String getUrlFull() {
        return urlFull;
    }

    public void setUrlFull(String urlFull) {
        this.urlFull = urlFull;
    }

    public String getUrlIp() {
        return urlIp;
    }

    public void setUrlIp(String urlIp) {
        this.urlIp = urlIp;
    }

    public String getUrlPort() {
        return urlPort;
    }

    public void setUrlPort(String urlPort) {
        this.urlPort = urlPort;
    }

    public String getUrlDbName() {
        return urlDbName;
    }

    public void setUrlDbName(String urlDbName) {
        this.urlDbName = urlDbName;
    }

    public String getUrlDbType() {
        return urlDbType;
    }

    public void setUrlDbType(String urlDbType) {
        this.urlDbType = urlDbType;
    }
}
