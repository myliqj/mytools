package org.my.tools.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.my.tools.util.MyStrUtils;

/**
 * SXSSFWorkbook ���� excel������
 * Created by liqj on 2019/1/19
 */
public class ExportExcel {

    /**
     * д�����
     *
     * @param sheet ��ǩҳ����
     * @param rowNum �ڼ��е��к�
     * @param values key:�ڼ��е��к�  value:ֵ
     */
    public static void createSheetHead(Sheet sheet, int rowNum, Map<Integer, Object> values) {
        Row row = sheet.createRow(rowNum);

        
        // ���ñ���ͷ��ʽ
        CellStyle colsy = sheet.getWorkbook().createCellStyle();

        Font font = sheet.getWorkbook().createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // ������ʾ
        colsy.setFont(font);

        // �߿�
        colsy.setBorderTop(CellStyle.BORDER_THIN);
        colsy.setBorderLeft(CellStyle.BORDER_THIN);
        colsy.setBorderRight(CellStyle.BORDER_THIN);
        colsy.setBorderBottom(CellStyle.BORDER_THIN);
        // ����
        colsy.setFillForegroundColor((short)23); // GREY_25_PERCENT.index=22
        colsy.setFillBackgroundColor((short)23); // GREY_50_PERCENT.index=23
        //colsy.setFillPattern(CellStyle.NO_FILL);
        colsy.setFillPattern(CellStyle.SOLID_FOREGROUND);
        // �Զ�����
        colsy.setWrapText(true);
        // ����
        colsy.setAlignment(CellStyle.ALIGN_CENTER);
        colsy.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        
        for (Integer cellNum : values.keySet()) {
            Cell cell = row.createCell(cellNum);
            Object value = values.get(cellNum);
            generateValue(value, cell);
            cell.setCellStyle(colsy);
        }
    }
    /**
     * @param row ��
     * @param cellNum �ڼ��е��к�
     * @param value   ֵ
     */
    public static void createCell(Row row, int cellNum, Object value) {
        Cell cell = row.createCell(cellNum);
        generateValue(value, cell);
    }
    private static void generateValue(Object value, Cell cell) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean createTemplateFile(String fileName,String SheetName,Map head,Map value) throws Exception {
        File file=new File(fileName);
        if(file.exists()){
            System.out.println("�ļ�["+fileName+"]�Ѵ��ڣ����ٴ���");
            return false;
        }

        SXSSFWorkbook wb = new SXSSFWorkbook();
        SXSSFSheet sheet = null;
        if(SheetName!=null && SheetName.length()>0) {
            sheet = wb.createSheet(SheetName);
        }else{
            sheet = wb.createSheet(); // Sheet0
        }

        int startRow = 0;
        if(head!=null) {
            createSheetHead(sheet, startRow, head);
            startRow++;
        }
        if(value!=null) {
            createSheetHead(sheet, startRow, value);
        }

        OutputStream fos = null;
        try {
//            System.out.println(fileName);
            fos = new FileOutputStream(file);
            wb.write(fos);
            fos.flush();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
            //return false;
        }finally {
            if (fos!=null){
                fos.close();
            }
            if(wb!=null){
                wb.dispose();
                wb.close();
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "resource", "unchecked" }) 
    public static boolean AddSheetToFile(String fileName,String SheetName,Map head,Map value) throws IOException {
        File file=new File(fileName);
        if(!file.exists()){
            System.out.println("�ļ�["+fileName+"]�����ڣ����ٴ���");
            return false;
        }
        InputStream is = new FileInputStream(file);
        XSSFWorkbook xwb = new XSSFWorkbook(is);
        SXSSFWorkbook wb = new SXSSFWorkbook(xwb);
        SXSSFSheet sheet = null;
        if(SheetName!=null && SheetName.length()>0) {
            try {
                sheet = wb.createSheet(SheetName);
            }catch (IllegalArgumentException e) {
                System.out.println("�Ѵ�����ͬ����ҳ["+SheetName+"]�����ٴ���");
                return false;
            }
        }else{
            sheet = wb.createSheet(); // Sheet0
        }

        int startRow = 0;
        if(head!=null) {
            createSheetHead(sheet, startRow, head);
            startRow++;
        }
        if(value!=null) {
            createSheetHead(sheet, startRow, value);
        }
        is.close();

        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            wb.write(fos);
            //fos.write(wb.getBytes());
            fos.flush();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if (fos!=null){
                fos.close();
            }
            if(wb!=null){
                wb.dispose();
                wb.close();
            }
        }
    }

//    public static void createCellOfRs(Row row, int cellNum, Object value,int xlsType,Map style) {
//        Cell cell = row.createCell(cellNum);
//        generateValueOfRs(value, cell, xlsType,style);
//    }

    public static int getXlsType(int v){
        /*
        int CELL_TYPE_NUMERIC = 0;
        int CELL_TYPE_STRING = 1;
        int CELL_TYPE_FORMULA = 2;
        int CELL_TYPE_BLANK = 3;
        int CELL_TYPE_BOOLEAN = 4;
        int CELL_TYPE_ERROR = 5;

        BIT             =  -7;  bool
        TINYINT         =  -6;  bool
        SMALLINT        =   5;  int
        INTEGER         =   4;  int
        BIGINT          =  -5;  double
        FLOAT           =   6;  double
        REAL            =   7;  double
        DOUBLE          =   8;  double
        NUMERIC         =   2;  double
        DECIMAL         =   3;  double
        CHAR            =   1;  str
        VARCHAR         =  12;  str
        LONGVARCHAR     =  -1;  str
        DATE            =  91;  date-d
        TIME            =  92;  date-t
        TIMESTAMP       =  93;  date-dt
        BINARY          =  -2;  str
        VARBINARY       =  -3;  str
        LONGVARBINARY   =  -4;  str
        NULL            =   0;
        OTHER           = 1111;
        JAVA_OBJECT     = 2000;
        DISTINCT        = 2001;
        STRUCT          = 2002;
        ARRAY           = 2003;
        BLOB            = 2004;  str
        CLOB            = 2005;  str
        REF             = 2006;
        DATALINK = 70;
        BOOLEAN = 16;            bool
        //--JDBC 4.0 -- 1.6
        ROWID = -8;              str
        NCHAR = -15;             str
        NVARCHAR = -9;           str
        LONGNVARCHAR = -16;      str
        NCLOB = 2011;            str
        SQLXML = 2009;           str
        //--JDBC 4.2 -- 1.8
        REF_CURSOR = 2012;
        TIME_WITH_TIMEZONE = 2013;
        TIMESTAMP_WITH_TIMEZONE = 2014;    */
        int r = 0; // str or other
        if (v==-5||v==6||v==7||v==8||v==2||v==3){
            r=1; // num-double
        }else if(v==4||v==5){
            r=2; // num-int
        }else if(v==-6||v==-7||v==16){
            r=3; // boolean
        }else if(v==91){
            r=4; // date-d
        }else if(v==92){
            r=5; // date-t
        }else if(v==93){
            r=6; // date-dt
        }
        return r;
    }

    public static void generateValueOfRs(Object value, Cell cell,int xlsType,int index,int[] maxLen) {
        // xls����: 0-�ַ�,1-Double,2-Integer,3-Boolean,4-Date-d,5-Date-t,6-Date-dt
        if(xlsType==0){
            if(! (value instanceof String)) {
                value = String.valueOf(value);
            }
            int len = ((String) value).getBytes().length;
            if(maxLen[index]<len) maxLen[index] = len;
            cell.setCellValue((String) value);
        }else if(xlsType==1){
            if(!(value instanceof Double)){
                value = Double.valueOf(String.valueOf(value));
            }
            int len = String.valueOf((Double) value).getBytes().length;
            if(maxLen[index]<len) maxLen[index] = len;

            cell.setCellValue((Double) value);
        }else if(xlsType==2){
            if(!(value instanceof Integer)){
                value = Integer.valueOf(String.valueOf(value));
            }
            int len = String.valueOf((Integer) value).getBytes().length;
            if(maxLen[index]<len) maxLen[index] = len;

            cell.setCellValue( (Integer)value);
        }else if(xlsType==3){
            if(!(value instanceof Boolean)){
                value = Boolean.valueOf(String.valueOf(value));
            }
            cell.setCellValue((Boolean) value);
        }else if(xlsType==4||xlsType==5||xlsType==6){
            if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof Calendar) {
                cell.setCellValue((Calendar) value);
            }else{
                // �޷����ø�ʽ,д�ַ�
                cell.setCellValue(String.valueOf(value));
            }
        }

        /*if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        }*/
    }
    public static boolean ResultSetToExecl(ResultSet rs,String outFileName,String outSheetName) throws Exception {
//        String outFileName = "d:\\a000003.xlsx";
//        String outSheetName = "����01";
//        ResultSet rs = null;

        // д��ͷ
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols=rsmd.getColumnCount();
        int[] coltype= new int[cols];
        int[] colxls= new int[cols];
        Map<Integer, Object> colnames = new HashMap<Integer, Object>();
        for (int i = 1; i <=cols; i++) {
            coltype[i-1] = rsmd.getColumnType(i); // java.sql.Types
            colxls[i-1] = getXlsType(coltype[i-1]);
            String cname= rsmd.getColumnName(i).toUpperCase();
            colnames.put(i-1,cname);
        }
        boolean isSucc = createTemplateFile(outFileName,outSheetName,colnames,null);
        if(!isSucc) return false;
        SXSSFWorkbook wb =  null;
        try {
            InputStream is = new FileInputStream(outFileName);
            XSSFWorkbook xwb = new XSSFWorkbook(is);
            wb = new SXSSFWorkbook(xwb);
            Sheet sheet = wb.getSheet(outSheetName);
            /*// yyyy-mm-dd hh:mm:ss
            // date
            CellStyle style_date = wb.createCellStyle();
            style_date.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
            // time
            CellStyle style_time = wb.createCellStyle();
            style_time.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
            // datetime
            CellStyle style_dt = wb.createCellStyle();
            style_dt.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

            Map<Integer,CellStyle> format_all = new HashMap<Integer,CellStyle>();
            format_all.put(4,style_date);
            format_all.put(5,style_time);
            format_all.put(6,style_dt);*/
            CellStyle style_date=null,style_time=null,style_dt=null;
            CellStyle style_default = wb.createCellStyle(); // �����߿�
            style_default.setBorderTop(CellStyle.BORDER_THIN);
            style_default.setBorderLeft(CellStyle.BORDER_THIN);
            style_default.setBorderRight(CellStyle.BORDER_THIN);
            style_default.setBorderBottom(CellStyle.BORDER_THIN);

            CellStyle[] css = new CellStyle[cols];
            for (int i = 1; i <=cols; i++) {
                int jtype = coltype[i-1]; // java.sql.Types
                if(jtype==91){
                    if (style_date==null){
                        style_date = wb.createCellStyle();
                        style_date.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
                    }
                    css[i-1] = style_date;
                }else if(jtype==92){
                    if (style_time==null){
                        style_time = wb.createCellStyle();
                        style_time.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
                    }
                    css[i-1] = style_time;
                }else if(jtype==93){
                    if (style_dt==null){
                        style_dt = wb.createCellStyle();
                        style_dt.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
                    }
                    css[i-1] = style_dt;
                }else if(jtype==2||jtype==3){
                    // NUMERIC         =   2;
                    // DECIMAL         =   3;
                    int scale = rsmd.getScale(i);
                    //int len = rsmd.getPrecision(i);
                    if (scale>0){
                        String dstr="0.";
                        while(dstr.length()<(scale+2)){
                            dstr+="0";
                        }
                        CellStyle style_1 = wb.createCellStyle();
                        style_1.setDataFormat(wb.createDataFormat().getFormat(dstr));
                        css[i-1] = style_1;
                    }
                }
                if(css[i-1]==null){
                    css[i-1] = style_default;
                }else{
                    css[i-1].setBorderTop(CellStyle.BORDER_THIN);
                    css[i-1].setBorderLeft(CellStyle.BORDER_THIN);
                    css[i-1].setBorderRight(CellStyle.BORDER_THIN);
                    css[i-1].setBorderBottom(CellStyle.BORDER_THIN);
                }
            }
            // д����
            int[] maxDataLen = new int[cols];
            // ��ʼ��Ϊ����
            for (int i = 0; i < cols; i++) {
                String colname=(String)colnames.get(Integer.valueOf(i));
                maxDataLen[i] = colname.getBytes().length;
                if(coltype[i]==91){
                    maxDataLen[i] = "yyyy-mm-dd".length();
                }else if(coltype[i]==92){
                    maxDataLen[i] = "hh:mm:ss".length();
                }else if(coltype[i]==93){
                    maxDataLen[i] = "yyyy-mm-dd hh:mm:ss".length();
                }
            }

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                Row row = sheet.createRow(rowCount); // ��һ�б����ˣ�index��0��ʼ��
                for (int i = 1; i <=cols; i++) {
                    Object obj = null;
                    obj = rs.getObject(i);
                    int jdbcType = coltype[i - 1];
                    int xlsType = colxls[i - 1];
                    if(obj==null){
                        //createCellOfRs(row,i-1,null,xlsType);
                        Cell cell = row.createCell(i-1);
                        if(css[i-1]!=null){
                            cell.setCellStyle(css[i-1]);
                        }
                        continue;
                    }
                    //if (jdbcType == Types.VARCHAR
                    //        || jdbcType == Types.DECIMAL
                    //        || jdbcType == Types.INTEGER){ // �������
                    if(jdbcType == Types.CLOB){ // 2005 CLOB
                        java.sql.Clob clob = (java.sql.Clob) obj;
                        obj = clob.getSubString((long)1, (int)clob.length());
                    }else if(jdbcType == Types.BLOB) { // 2004 BLOB
                        java.sql.Blob blob = (java.sql.Blob) obj;
                        byte[] val = blob.getBytes((long) 1, (int) blob.length());
                        obj = new String(val);
                    }
                    //createCellOfRs(row,i-1,obj,xlsType,format_all);
                    Cell cell = row.createCell(i-1);
                    generateValueOfRs(obj, cell, xlsType,i-1,maxDataLen);
                    if(css[i-1]!=null){
                        cell.setCellStyle(css[i-1]);
                    }

                }
                if (rowCount%1000==0){
                    System.out.println("[������]����������" + rowCount);
                }
            }
            // �����п�
            // ʹ��ģ������������ܻ�ȡ��Ӧ�ֶ� Row row = sheet.getRow(0); ==>> null
            for (int i = 0; i < cols; i++) {
                //String colname=(String)colnames.get(Integer.valueOf(i));
                //int collen = colname.length();
                int maxlen = maxDataLen[i];
                //int jtype = coltype[i];
                int setlen = maxlen;
                //if(!(jtype==91||jtype==92||jtype==93)){
                //}
                if (setlen>25){
                    setlen=25;
                }
                if (setlen<4){
                    setlen=4;
                }
                //System.out.println(colname+",i="+i+",maxlen="+maxlen+",set-collen="+(setlen*260));
                sheet.setColumnWidth(i,setlen*265); // 1/256���ַ�

            }

            System.out.println("[���]����������" + rowCount);

            if (rowCount>0) {
                OutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(outFileName));
                    wb.write(fos);
                    fos.flush();
                } finally {
                    if (fos != null) fos.close();
                }
            }
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        } finally {
            // ɾ����ʱ�ļ�
            if(wb!=null){
                wb.dispose();
                wb.close();
            }
        }

    }

    public static boolean toExcel(String sql, Connection conn, String fileName,String sheetName)throws Exception{
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return ResultSetToExecl(rs,fileName,sheetName);
        }finally{
            if(stmt!=null) stmt.close();
        }

    }
    

    /**
     * �ѽ�����������ݵ������ļ� xlxs ��
     * @param rs �򿪵Ľ����
     * @param inFileName ����ģ������,�����������ʹ�� rs �����ؽ�
     * @param inSheetName ����ģ��� SheetName
     * @param outFileName ����ļ�����, ����������ģ��������ͬ���൱�ڸ���ģ���ļ���
     * @param outSheetName ����ļ��� SheetName
     * @param startRow ��ʼ����,��1��ʼ��һ��ģ����һ�б������ֵ����2��ʼ��
     * @param isAutoWidth �п��Ƿ��Զ���Ӧ����, ����̫�໹�ǲ�Ҫ���� ���п�Χ 4-25�ַ�
     * @return �Ƿ������ļ��ɹ�
     * @throws Exception
     */
    public static int ResultSetToExeclStartRow(ResultSet rs    		
    		,String inFileName,String inSheetName
    		,String outFileName,String outSheetName
    		,int startRow,boolean isAutoWidth,int maxColumnWidth) throws Exception {
        // д��ͷ
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols=rsmd.getColumnCount();
        int[] coltype= new int[cols];
        int[] colxls= new int[cols];
        Map<Integer, Object> colnames = new HashMap<Integer, Object>();
        for (int i = 1; i <=cols; i++) {
            coltype[i-1] = rsmd.getColumnType(i); // java.sql.Types
            colxls[i-1] = getXlsType(coltype[i-1]);
            String cname= rsmd.getColumnName(i).toUpperCase();
            colnames.put(i-1,cname);
        }
        File inFile = new File(inFileName); 
        if (!inFile.exists()){
            boolean isSucc = createTemplateFile(inFileName,inSheetName,colnames,null);
            if(!isSucc) return -1;
        }
        SXSSFWorkbook wb =  null;
        try {
            InputStream is = new FileInputStream(inFileName);
            XSSFWorkbook xwb = new XSSFWorkbook(is);
            wb = new SXSSFWorkbook(xwb);
            
            if (MyStrUtils.isNotEmpty(outSheetName) && !inSheetName.equals(outSheetName)){
            	int in_index = wb.getSheetIndex(inSheetName);
            	if(in_index<0) in_index = 0;
            	wb.setSheetName(in_index, outSheetName);
            }
            Sheet sheet = wb.getSheet(outSheetName);
            /*// yyyy-mm-dd hh:mm:ss
            // date
            CellStyle style_date = wb.createCellStyle();
            style_date.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
            // time
            CellStyle style_time = wb.createCellStyle();
            style_time.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
            // datetime
            CellStyle style_dt = wb.createCellStyle();
            style_dt.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

            Map<Integer,CellStyle> format_all = new HashMap<Integer,CellStyle>();
            format_all.put(4,style_date);
            format_all.put(5,style_time);
            format_all.put(6,style_dt);*/
            CellStyle style_date=null,style_time=null,style_dt=null;
            CellStyle style_default = wb.createCellStyle(); // �����߿�
            style_default.setBorderTop(CellStyle.BORDER_THIN);
            style_default.setBorderLeft(CellStyle.BORDER_THIN);
            style_default.setBorderRight(CellStyle.BORDER_THIN);
            style_default.setBorderBottom(CellStyle.BORDER_THIN);

            CellStyle[] css = new CellStyle[cols];
            for (int i = 1; i <=cols; i++) {
                int jtype = coltype[i-1]; // java.sql.Types
                if(jtype==91){
                    if (style_date==null){
                        style_date = wb.createCellStyle();
                        style_date.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
                    }
                    css[i-1] = style_date;
                }else if(jtype==92){
                    if (style_time==null){
                        style_time = wb.createCellStyle();
                        style_time.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
                    }
                    css[i-1] = style_time;
                }else if(jtype==93){
                    if (style_dt==null){
                        style_dt = wb.createCellStyle();
                        style_dt.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
                    }
                    css[i-1] = style_dt;
                }else if(jtype==2||jtype==3){
                    // NUMERIC         =   2;
                    // DECIMAL         =   3;
                    int scale = rsmd.getScale(i);
                    //int len = rsmd.getPrecision(i);
                    if (scale>0){
                        String dstr="0.";
                        while(dstr.length()<(scale+2)){
                            dstr+="0";
                        }
                        CellStyle style_1 = wb.createCellStyle();
                        style_1.setDataFormat(wb.createDataFormat().getFormat(dstr));
                        css[i-1] = style_1;
                    }
                }
                if(css[i-1]==null){
                    css[i-1] = style_default;
                }else{
                    css[i-1].setBorderTop(CellStyle.BORDER_THIN);
                    css[i-1].setBorderLeft(CellStyle.BORDER_THIN);
                    css[i-1].setBorderRight(CellStyle.BORDER_THIN);
                    css[i-1].setBorderBottom(CellStyle.BORDER_THIN);
                }
            }
            // д����
            int[] maxDataLen = new int[cols];
            // ��ʼ��Ϊ����
            for (int i = 0; i < cols; i++) {
                String colname=(String)colnames.get(Integer.valueOf(i));
                maxDataLen[i] = colname.getBytes().length;
                if(coltype[i]==91){
                    maxDataLen[i] = "yyyy-mm-dd".length();
                }else if(coltype[i]==92){
                    maxDataLen[i] = "hh:mm:ss".length();
                }else if(coltype[i]==93){
                    maxDataLen[i] = "yyyy-mm-dd hh:mm:ss".length();
                }
            }

            int startRowIndex = startRow-2;
            int rowCount = 0;
            while (rs.next()) {
                rowCount++; startRowIndex++;
                Row row = sheet.createRow(startRowIndex); // index��0��ʼ��
                for (int i = 1; i <=cols; i++) {
                    Object obj = null;
                    obj = rs.getObject(i);
                    int jdbcType = coltype[i - 1];
                    int xlsType = colxls[i - 1];
                    if(obj==null){
                        //createCellOfRs(row,i-1,null,xlsType);
                        Cell cell = row.createCell(i-1);
                        if(css[i-1]!=null){
                            cell.setCellStyle(css[i-1]);
                        }
                        continue;
                    }
                    //if (jdbcType == Types.VARCHAR
                    //        || jdbcType == Types.DECIMAL
                    //        || jdbcType == Types.INTEGER){ // �������
                    if(jdbcType == Types.CLOB){ // 2005 CLOB
                        java.sql.Clob clob = (java.sql.Clob) obj;
                        obj = clob.getSubString((long)1, (int)clob.length());
                    }else if(jdbcType == Types.BLOB) { // 2004 BLOB
                        java.sql.Blob blob = (java.sql.Blob) obj;
                        byte[] val = blob.getBytes((long) 1, (int) blob.length());
                        obj = new String(val);
                    }
                    //createCellOfRs(row,i-1,obj,xlsType,format_all);
                    Cell cell = row.createCell(i-1);
                    generateValueOfRs(obj, cell, xlsType,i-1,maxDataLen);
                    if(css[i-1]!=null){
                        cell.setCellStyle(css[i-1]);
                    }

                }
                if (rowCount%1000==0){
                    System.out.println("[������]����������" + rowCount);
                }
            }
            // �����п�
            // ʹ��ģ������������ܻ�ȡ��Ӧ�ֶ� Row row = sheet.getRow(0); ==>> null
            if (isAutoWidth){
            	System.out.println(" �Զ��п�");
	            for (int i = 0; i < cols; i++) {
	                //String colname=(String)colnames.get(Integer.valueOf(i));
	                //int collen = colname.length();
	                int maxlen = maxDataLen[i];
	                //int jtype = coltype[i];
	                int setlen = maxlen;
	                //if(!(jtype==91||jtype==92||jtype==93)){
	                //}
	                if (setlen>maxColumnWidth){
	                    setlen=maxColumnWidth;
	                }
	                if (setlen<4){
	                    setlen=4;
	                }
	                System.out.println("   index="+i+",maxlen="+maxlen+",setlen="+setlen+",set-collen="+(setlen*260));
	                sheet.setColumnWidth(i,setlen*265); // 1/256���ַ�
	
	            }
            }

            System.out.println("[���]����������" + rowCount);

            if (rowCount>0) {
                OutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(outFileName));
                    wb.write(fos);
                    fos.flush();
                    System.out.println("����ɹ���" + outFileName);
                } finally {
                    if (fos != null) fos.close();
                }
            }
            return rowCount;
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
            //return false;
        } finally {
            // ɾ����ʱ�ļ�
            if(wb!=null){
                wb.dispose();
                wb.close();
            }
        }

    }

    public static void main(String[] args) throws Exception {

        String sql= "select TABSCHEMA,TABNAME,CREATE_TIME,COLCOUNT �ֶ�����,TABLEID,CARD,TBSPACEID,REMARKS" +
                ",STATISTICS_PROFILE,AVGROWCOMPRESSIONRATIO,dec(CODEPAGE,6,2) dec_6_2,AVGCOMPRESSEDROWSIZE" +
                ",AVGROWCOMPRESSIONRATIO\n" +
                ",AVGROWSIZE\n" +
                ",PCTROWSCOMPRESSED" +
                " from syscat.tables a where a.type='T' order by TABSCHEMA,TABNAME fetch first 1011 row only";
        String fileName = "d:\\a000017.xlsx";
        File f = new File(fileName);
        if (f.exists()){
            boolean isok=f.delete();
            System.out.printf("ɾ���ļ���"+fileName + " = " + isok +"\n");
            f = null;
            Thread.sleep(1000);
        }
        String sheetName = "tables";
        //sql="select * from syscat.columns";
        Class.forName("com.ibm.db2.jcc.DB2Driver");
        Connection conn = DriverManager.getConnection("jdbc:db2://{#ip#}:{#port#}/{#dbname#}", "user", "pwd");        
        try{
           toExcel(sql,conn,fileName,sheetName);
        }finally {
            if(conn!=null) conn.close();
        }


//        if(1==1) return;
//
//        Map<Integer, Object> firstTitles = new HashMap<Integer, Object>();
//        firstTitles.put(0, "����1:");
//        firstTitles.put(2, "test12221");
//        firstTitles.put(7, "ʱ��:");
//        firstTitles.put(8, "2019-09-11");
//
//        //createTemplateFile("d:\\a000002.xlsx","",firstTitles,null);
//        AddSheetToFile("d:\\a000002.xlsx","a3",firstTitles,null);
    }
    
    @SuppressWarnings("resource")
	public static void main2(String[] args) throws Exception {

        //����ģ���ļ�
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("F:\\test\\ceshi.xlsx"));
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWorkbook, 1000);

        //�����ļ�
        File file = new File("F:\\test\\test2.xlsx");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 2; i++) {
            Sheet sheet = workbook.getSheet("sheet" + (i + 1));
            if (sheet == null) {
                sheet = workbook.createSheet("sheet" + (i + 1));
            }
//            ���ɱ���
            Map<Integer, Object> firstTitles = new HashMap<Integer, Object>();
            firstTitles.put(0, "����:");
            firstTitles.put(1, "test12221");
            firstTitles.put(7, "ʱ��:");
            firstTitles.put(8, "2017-09-11");
            createSheetHead(sheet, 0, firstTitles);

            Map<Integer, Object> twoTitles = new HashMap<Integer, Object>();
            twoTitles.put(0, "���ţ�");
            twoTitles.put(1, "test12221");
            twoTitles.put(2, "����:");
            twoTitles.put(3, "aaaa");
            createSheetHead(sheet, 1, twoTitles);

            for (int rownum = 2; rownum < 100000; rownum++) {
                Row row = sheet.createRow(rownum);
                int k = -1;
                createCell(row, ++k, "�� " + rownum + " ��");
                createCell(row, ++k, 34343.123456789);
                createCell(row, ++k, "23.67%");
                createCell(row, ++k, "12:12:23");
                createCell(row, ++k, "2014-10-<11 12:12:23");
                createCell(row, ++k, "true");
                createCell(row, ++k, "false");
                createCell(row, ++k, "fdsa");
                createCell(row, ++k, "123");
                createCell(row, ++k, "321");
                createCell(row, ++k, "3213");
                createCell(row, ++k, "321");
                createCell(row, ++k, "321");
                createCell(row, ++k, "43432");
                createCell(row, ++k, "54");
                createCell(row, ++k, "fal45se");
                createCell(row, ++k, "fal6se");
                createCell(row, ++k, "fal64321se");
                createCell(row, ++k, "fal43126se");
                createCell(row, ++k, "432432");
                createCell(row, ++k, "432432");
                createCell(row, ++k, "r54");
                createCell(row, ++k, "543");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1a");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
                createCell(row, ++k, "few1");
            }

        }
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);

        System.out.println((System.currentTimeMillis()-start));
        out.close();
    }
}
