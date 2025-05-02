/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import entities.TableEntity;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author operator
 */
public class Runquery {

    private String tableName;// имя таблицы базы данных в/из которой передаются данные
    private String[] columnName = null;// массив назименований столбцов
    private Class[] columnClass = null;// массив типов данных столбцов
    
    public Runquery(String tableName) {
        this.tableName = tableName;
    }

    public Runquery() {
    }
    
    /**
     * 
     * @param sqlString ������ - ������ �� ��������� ������
     * @return ����� ����� �� �������� �������
     */
    public List<TableEntity> getEntities(String sqlString){
        List<TableEntity> entities = new ArrayList<>();
//        System.out.println("sqlString=" + sqlString);
        int index = 1;// �������������� ��������� ����� ������
        try {
            // �������� ����� ������
            ResultSet rs = JDBCConnection.getStat().executeQuery(sqlString);
            while(rs.next()){
                TableEntity e = new TableEntity(rs.getInt("ID"), index);// ��������� ��������
                entities.add(e);// ��������� � ������
                index++;// ����������� �������

            }
            return entities;
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return null;
        }
    }
    
    /**
     *
     * @param sqlString ������ ������ �� ������� ������
     * @return ������, ���������� ���������� ������
     */
    public List<Object[]> getQueryEntities(String sqlString){
        try {
            List<Object[]> entities;
//            System.out.println("sqlstring = " + sqlString);
            
            // �������� ��������� ���������� ����������
            Statement stat = JDBCConnection.getStat();
            entities = new ArrayList<>();
            // ������ ����� ������
            ResultSet rs = stat.executeQuery(sqlString); // �������� ��������� ����������

            ResultSetMetaData rsmd = rs.getMetaData();// �������� ���������� �������
            int numCol = rsmd.getColumnCount();// ���������� �������� �������
//            System.out.println("numcol = " + numCol);
//            System.out.println("row = " + rs.first());
            // ���������� ������� � ��������� ������� ��� ����� � ����� ������
            columnClass = new Class[numCol + 1];
            columnName = new String[numCol + 1];
            columnName[0] = "�";
            columnClass[0] = Integer.class;
//            getDataClassValue(rsmd);
            for(int i = 1; i < numCol + 1; i++){
                columnName[i] = rsmd.getColumnLabel(i);
                columnClass[i] = getDataClassValue(rsmd.getColumnType(i));
//                System.out.println("Class - " + columnClass[i].getTypeName());
            }
            int index = 1;// ���������� ��������� ����� ������
//            System.out.println("������ � ����, ����� " + rs.isClosed());
            while (rs.next()) {
//                System.out.println("�� � �����");
                Object[] e = new Object[numCol + 1];// ��������� ��������
                // ��������� �������� ������� �����
                e[0] = index;
                for(int i = 1; i < e.length; i++){
                    e[i] = rs.getString(i);
//                    System.out.println("e[i]= " + e[i]);
                }
                entities.add(e);// ��������� � ������
                index++;// ����������� �������
            }
            
            
            return entities;
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return null;
        }
        
    }
    
    public String[] getColumnName(){
        return columnName;
    }
    
    public Class[] getColumnClass(){
        return columnClass;
    }
    
    /**
     * 
     * @param fieldName ������ � �������������� ����� �������, � �������
     * ����� ����������� ������, ���������� �������
     * @param fieldValue ������, ���������� ����� ����������� ������ ��������, ��
     * ���������� ������ ��������������� ������� ������� ����������� ����������
     * @param param ������ ������������� ���������� �������� ��� ���������� � �������
     * @return � ������ ������ ���������� true ����� false
     */
    public boolean addEntity(String fieldName, String fieldValue, int[] param){
        boolean retval;
        String sqlQuery = "INSERT INTO " + tableName + "(" + fieldName + ") " +
                "VALUES(" + fieldValue + ");";
        int i = 0;
        try {
            // ������ �������������� ���������� ��� ���������� �������� ���������� ������
            PreparedStatement prst = JDBCConnection.getPrepstat(sqlQuery);
            if(prst != null){
                // ���� �������������� ���������� �������,
                // ����� � ���������
                for(int j = 0; j < param.length; j++)
                    prst.setInt(j + 1, param[j]);
                i = prst.executeUpdate();// �������� ��������� ����������
                prst.clearParameters();// ������� ���������
                prst.close();// ��������� ����������
            }
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return false;
        }
        retval = i > 0;
        return retval;
    }
    
    /**
     * 
     * @param fieldName ������ � �������������� ����� �������, � �������
     * ����� ����������� ������, ���������� �������
     * @param param ������ ������������� ���������� �������� ��� ���������� � �������
     * @param fieldValue ������, ���������� ����� ����������� ������ ��������, ��
     * ���������� ������ ��������������� ������� ������� ����������� ����������
     * @return � ������ ������ ���������� true ����� false
     */
    public boolean addEntity(String fieldName, String fieldValue, String[] param){
        boolean retval;
        String sqlQuery = "INSERT INTO " + tableName + "(" + fieldName + ") " +
                "VALUES(" + fieldValue + ");";
        System.out.print(sqlQuery);
        int i = 0;
        try {
            // ������ �������������� ���������� ��� ���������� �������� ���������� ������
            PreparedStatement prst = JDBCConnection.getPrepstat(sqlQuery);
            if(prst != null){
                // ���� �������������� ���������� �������,
                // ����� � ���������
                for(int j = 0; j < param.length; j++)
                    prst.setString(j + 1, param[j]);
                i = prst.executeUpdate();// �������� ��������� ����������
                prst.clearParameters();// ������� ���������
                prst.close();// ��������� ����������
            }
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return false;
        }
        retval = i > 0;
        return retval;
    }
    
    /**
     * 
     * @param fieldName ������ � �������������� ����� �������, � �������
     * ����� ����������� ������, ���������� �������
     * @param fieldValue ������, ���������� ����� ����������� ������ �������� (��
     * ���������� ������ ��������������� ������� ������� ����������� ����������)
     * @param classValue ������, ���������� ���� ������ ����������
     * @param param ������ �������� ���������� ��� ���������� � �������
     * @return � ������ ������ ���������� TRUE ����� FALSE
     */
    public boolean addEntity(String fieldName, String fieldValue, Class[] classValue, Object[] param){
        boolean retval;
        String sqlQuery = "INSERT INTO " + tableName + "(" + fieldName + ") " +
                "VALUES(" + fieldValue + ");";
        System.out.print(sqlQuery);
        int i = 0;
        try {
            // ������ �������������� ���������� ��� ���������� �������� ���������� ������
            PreparedStatement prst = JDBCConnection.getPrepstat(sqlQuery);
            if(prst != null){
                
                // ���� �������������� ���������� �������,
                // ����� � ���������
                for(int j = 0; j < param.length; j++){
                    if(classValue[j] == String.class){
                        prst.setString(j + 1, (String) param[j]);
                    } else if(classValue[j] == Integer.class){
                        prst.setInt(j + 1, (int) param[j]);
                    } else if(classValue[j] == Double.class){
                        prst.setDouble(j + 1, (double) param[j]);
                    } else if(classValue[j] == Float.class){
                        prst.setFloat(j + 1, (float) param[j]);
                    } else if(classValue[j] == Long.class){
                        prst.setLong(j + 1, (long) param[j]);
                    } else if(classValue[j] == Short.class){
                        prst.setShort(j + 1, (short) param[j]);
                    } else if(classValue[j] == Boolean.class){
                        prst.setBoolean(j + 1, (boolean) param[j]);
                    } else if(classValue[j] == Time.class){
                        prst.setTime(j + 1, (Time) param[j]);
                    } else if(classValue[j] == Date.class){
                        prst.setDate(j + 1, (Date) param[j]);
                    } else if(classValue[j] == Timestamp.class){
                        prst.setTimestamp(j + 1, (Timestamp) param[j]);
                    } else if(classValue[j] == BigDecimal.class){
                        prst.setBigDecimal(j + 1, (BigDecimal) param[j]);
                    } else{
                        prst.setObject(j + 1, param[j]);
                    }
                    
                }
                
                i = prst.executeUpdate();// �������� ��������� ����������
                prst.clearParameters();// ������� ���������
                prst.close();// ��������� ����������
            }
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return false;
        }
        retval = i > 0;
        return retval;
    }
    
    /**
     * ��������� ����� ������ � ��������� ������� ���� ������
     * @param procedureName ��� ���������� �������� ��������� ��� ���������� ����� ������
     * @param inParameters ������ ������� ����������
     * @param outParameters ������ �������� ����������
     * @return ����������������� ��� ����������� ������
     * @throws java.sql.SQLException
     */
    public int[] addEntity(String procedureName, int[] inParameters, int[] outParameters) throws SQLException {
        // ��������� ��������� ��� ������ ���������� �������� ���������
        int charParam = (inParameters.length + outParameters.length);
        String charAt = "";// ������ ��� �������� ����������� ����������
        for(int i = 0; i < charParam; i++) {
            charAt = charAt + "?,";// ��������� ������ � ��������� ����������� ����������
        }
        charAt = charAt.substring(0, charAt.length() - 1);// ������� ��������� ������
        String sqlQuery = "{call " + procedureName + " " + charAt + "}";
//        System.out.println("sqlquery - " + sqlQuery);
        CallableStatement myStmt = JDBCConnection.getCallableStat(sqlQuery);// �������� ���������
        if(myStmt != null) {
            // ���� ����� ������ ���������, ������������ ���������
//            System.out.println("inLenth = " + inParameters.length);
//            System.out.println("outLenth = " + outParameters.length);
            int index = 0;// ����� ������� ���������
            // ������������ ������� ���������
            for(int i = 1; i <= inParameters.length; i++) {
                myStmt.setInt(i, inParameters[i - 1]);
                index = i;// ���������� ������� �������� ���������
            }
            // ������������ �������� ���������
            for(int i = 1; i <= outParameters.length; i++) {
                index += i;// ����������� ������� ��� ��������� ���������
                myStmt.registerOutParameter(index, java.sql.Types.INTEGER);
            }
            // ��������� ���������
            int executeUpdate = myStmt.executeUpdate();
            // ��������� ������ �������� ����������
            for(int i = 1; i <= outParameters.length; i++) {
                outParameters[i - 1] = myStmt.getInt(i);
            }
            
            return outParameters;
        } else return null;
        
    }
    
    /**
     * 
     * @param identifier ������������� ��������� ������
     * @return � ������ ������ ���������� true ����� false
     */
    public boolean deleteEntity(int identifier){
        boolean retval;
        String sqlQuery = "DELETE FROM " + tableName + 
                " A WHERE A.ID=" + identifier + ";";
        int i;
        try {
            i = JDBCConnection.getStat().executeUpdate(sqlQuery);// �������� ������ �� ��������
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return false;
        }
        retval = i > 0;// ��������� ��������� ���������� �������
        return retval;
    }
    
    /**
     * 
     * @param sqlQuery ������ - ������ �� ������� ������
     * @param fieldNumber ����� ���� � �������, �������� �������� ������������
     * @return �������� ���������� ���� �������
     */
    public Object getFieldValue(String sqlQuery, int fieldNumber){
        Object retval = null;
        try {
            ResultSet rs = JDBCConnection.getStat().executeQuery(sqlQuery);
            if(rs.next())
                retval = rs.getString(fieldNumber);
        } catch (SQLException ex) {
            showErrorMessage(ex);
        }
        return retval;
    }
    
    /**
     * 
     * @param sqlQuery ������ - ������ �� ���������� ������
     * @return � ������ ������ true, ����� ���������� false
     */
    public boolean updateFieldValue(String sqlQuery) {
        boolean retval;
        try {
            retval = JDBCConnection.getStat().execute(sqlQuery);
            // ��������� ������� ���������� � ���������� ���������
            if(JDBCConnection.getStat().getUpdateCount() > 0) retval = true;
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return false;
        }
        return retval;
    }
    
    public Object[] getEntity(String sqlQuery){
//        System.out.println(sqlQuery);
        Object[] retval;
        try {
            
            ResultSet rs = JDBCConnection.getStat().executeQuery(sqlQuery);
            // ���������� ������
            ResultSetMetaData rsmd = rs.getMetaData();
            retval = new Object[rsmd.getColumnCount()];// ���������� ������ �������
            
//            System.out.println("lenght =" + rsmd.getColumnCount());
            if(rs.first() == true){
                // ������ ���� � ������ ������� ����� ���������� ����������
                // ����� ��������
                getDataTypesValue(retval, rs, rsmd);
                return retval;
            } else {return null;}
        } catch (SQLException ex) {
            showErrorMessage(ex);
            return null;
        }
//        return rs;
    }
    
    public void getColumns(String sqlQuery){
        try {
            ResultSet rs = JDBCConnection.getStat().executeQuery(sqlQuery);
            // ���������� ������
            ResultSetMetaData rsmd = rs.getMetaData();
            
            // ����� ������� ��������
            columnClass = new Class[rsmd.getColumnCount()];
            columnName = new String[rsmd.getColumnCount()];
            
            // �������� ������������ � ���� ������ ��������
            for(int i = 0; i < columnClass.length; i++){
                columnName[i] = rsmd.getColumnName(i+1);
                columnClass[i] = getDataClassValue(rsmd.getColumnType(i + 1));
            }
        } catch (SQLException ex) {
            showErrorMessage(ex);
        }
            
    }
    
    
    private void getDataTypesValue(Object[] retval, ResultSet rs, 
            ResultSetMetaData rsmd) throws SQLException{
        for(int i = 0; i < retval.length; i++){
            // ���������� ��� ������ ����
            switch(rsmd.getColumnType(i+1)){
                case Types.CHAR:
                case Types.NVARCHAR:
                    retval[i] = rs.getString(i+1);
                    break;
                case Types.INTEGER:
                    retval[i] = rs.getInt(i+1);
                    break;
                case Types.DOUBLE:
                case Types.FLOAT:
                    retval[i] = rs.getDouble(i+1);
                    break;
                case Types.REAL:
                    retval[i] = rs.getFloat(i+1);
                    break;
                case Types.BIGINT:
                    retval[i] = rs.getLong(i+1);
                    break;
                case Types.SMALLINT:
                    retval[i] = rs.getShort(i+1);
                    break;
                case Types.BOOLEAN:
                    retval[i] = rs.getBoolean(i+1);
                    break;
                case Types.TIME:
                    retval[i] = rs.getTime(i+1);
                    break;
                case Types.DATE:
                    retval[i] = rs.getDate(i+1);
                    break;
                case Types.TIMESTAMP:
                    retval[i] = rs.getTimestamp(i+1);
                    break;
                case Types.DECIMAL:
                case Types.NUMERIC:
                    retval[i] = rs.getBigDecimal(i+1);
                    break;
                default:
                    retval[i] = rs.getString(i+1);
            }
//            System.out.println("retval[" + i + "]=" + retval[i].toString());
        }
    }
    
    
    private Class getDataClassValue(int type) throws SQLException{
        // �������� ������������ � ���� ������ ��������
        switch(type){
            case Types.CHAR:
            case Types.NVARCHAR:
                return String.class;
            case Types.INTEGER:
                return Integer.class;
            case Types.DOUBLE:
            case Types.FLOAT:
                return Double.class;
            case Types.REAL:
                return Float.class;
            case Types.BIGINT:
                return Long.class;
            case Types.SMALLINT:
                return Short.class;
            case Types.BOOLEAN:
                return Boolean.class;
            case Types.TIME:
                return Time.class;
            case Types.DATE:
                return String.class;
            case Types.TIMESTAMP:
                return Timestamp.class;
            case Types.DECIMAL:
            case Types.NUMERIC:
                return BigDecimal.class;
            default:
                return String.class;
        }
        
    }
    
    private void showErrorMessage(SQLException ex){
        JOptionPane.showMessageDialog(null, "��������� ������ �� ����� ������������ ��������!\n\r" +
                "������: " + ex.getLocalizedMessage() + "\n\r" +
                "��� ������: " + ex.getErrorCode(), "AbonentGaz", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(Runquery.class.getName()).log(Level.SEVERE, null, ex);
    }
    
}
