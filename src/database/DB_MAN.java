package database; // 패키지명 확인 필수!

import java.sql.*;
import java.io.*;

public class DB_MAN {
    // MySQL 드라이버 및 URL (팀원 공통 설정)
    String strDriver = "com.mysql.cj.jdbc.Driver";
    String strURL = "jdbc:mysql://localhost:3306/team_bbs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"; 
    String strUser = "root";
    String strPWD = "Inha@1958"; 

    Connection DB_con;
    Statement DB_stmt;
    
    // DB 연결 메소드
    public void dbOpen() throws IOException {
        try {
            Class.forName(strDriver);
            DB_con = DriverManager.getConnection(strURL, strUser, strPWD);
            DB_stmt = DB_con.createStatement();
            System.out.println("MySQL(team_bbs) 연결 성공!");
        } catch (Exception e) {
            System.out.println("연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // DB 해제 메소드
    public void dbClose() throws IOException {
        try {
            if (DB_stmt != null) DB_stmt.close();
            if (DB_con != null) DB_con.close();
        } catch (SQLException e) {
            System.out.println("닫기 오류: " + e.getMessage());
        }
    }
    
    // 다른 클래스(DAO 등)에서 Connection 객체를 가져다 쓸 수 있게 함
    public Connection getConnection() {
        return DB_con;
    }
}