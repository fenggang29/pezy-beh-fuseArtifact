package com.pezy.datafuse.sybase;

import java.sql.*;

/**
 * Created by 冯刚 on 2018/1/17.
 */
public class TestSybase {
    public  static void main(String[] args){
        try{
        Connection conn = getConn();

            System.out.println("---------3=="+conn+"=======");
        Statement stmt = conn
                .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            System.out.println("---------4=========");
        String sql = "select id,name,crdate from dbo.sysobjects where type='U'"; // 表
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println("oject_id:"+rs.getString(1)+",oject_name:"+rs.getString(2)); // 取得第二列的值
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    }
    public static Connection getConn() {

        String dbString = "";
        String dbDriveString = "";
        String userName = "";
        String passwd = "";


        dbString = "jdbc:sybase:Tds:132.98.16.164:5000/iq_test?charset=cp936&jconnect_version=3";
        dbDriveString = "com.sybase.jdbc3.jdbc.SybDriver";
        userName = "dfgx_dm";
        passwd = "dFgx@133";

        Connection conn = null;
        try {
            Class.forName(dbDriveString);

            conn = DriverManager.getConnection(dbString, userName, passwd);
        } catch (ClassNotFoundException e1) {
            System.out.println("---------1=========");
            e1.printStackTrace();
        } catch (SQLException e) {
            System.out.println("---------2=========");
            e.printStackTrace();
        }
        return conn;
    }
}
