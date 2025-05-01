/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import connection.Runquery;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class TableEntity {
    
    private int id;// ��� �������� �� ������� ���� ������
    private int index;// ���������� ����� ��������
    private String tablename;// ��� �������, ������� ������������ ��������
    
    public TableEntity() {
    }

    public TableEntity(int id) {
        this.id = id;
    }

    public TableEntity(int id, int index) {
        this.id = id;
        this.index = index;
    }
    
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableEntity other = (TableEntity) obj;
        if (this.id != other.id) {
            return false;
        }
        return this.index == other.index;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }

    /**
     * @param tablename the tablename to set
     */
    public void setTablename(String tablename) {
        this.tablename = tablename;
    }
    
    /**
     * ���������� ��������� ���� ������� ���� ������
     * @param fieldname ������������ ����
     * @param fieldvalue �������� ��� ����������
     * @return � ������ ������ - true, ����� - false
     */
    public boolean updateEntity(String fieldname, Object fieldvalue){
        String sqlQuery = "UPDATE " + tablename + " B SET B." + fieldname +
                "=" + fieldvalue + " WHERE B.ID=" + id + ";";
        System.out.println("sql=" + sqlQuery);
        Runquery rq = new Runquery();
        return rq.updateFieldValue(sqlQuery);
    }
    
    /**
     * ���������� ��������� ���� ������� ���� ������
     * @param Col ����� ������������ ������� (���� �������)
     * @param value �������� ��� ����������
     * @return � ������ ������ - true, ����� - false
     */
    public boolean updateEntity(int Col, Object value){
        return false;
    }
    
    /**
     * ������������ ������ ����� �������� � ���� �������
     * @param sqlQuery ������-������ �� ������� �������� ����� ��������
     * @return ���������� ������ ������ ����� ��������
     */
    public Object[] getFieldValues(String sqlQuery){
        Runquery rq = new Runquery();
        return rq.getEntity(sqlQuery);
        
    }
    
    /**
     * ������������ ������ ����� �������� � ���� �������
     * @return ���������� ������ ������ ����� ��������
     */
    public Object[] toDataArray(){
        return getEntity();
    }
    
    
    /**
     * �������� ������ �� ��������� ������� ���� ������ �� ��������� ��������
     * @return ���������� ������ ������ ����� ��������
     */
    private Object[] getEntity(){
        String sqlQuery = "SELECT * FROM " + tablename + 
                " A WHERE A.ID=" + id + ";";// ������ - ������ �� ������� ������ �� �������
        
        Runquery rq = new Runquery();
        Object[] retval = rq.getEntity(sqlQuery);
        retval[0] = index;// ������ ������� ������� �������� ����� �������� � ������
        return retval;
    }
}
