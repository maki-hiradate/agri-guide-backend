package com.agriguide.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/sensor-data")
public class SensorDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // データベース接続情報
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agriguide_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root1234"; // あなたが設定したパスワード

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // レスポンスの設定
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        // CORS設定
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // JDBCドライバーのロード
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // データベース接続
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // 最新のデータを取得
            String sql = "SELECT speed, distance FROM sensor_data ORDER BY created_at DESC LIMIT 1";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            String data;
            if (rs.next()) {
                // データベースからデータを取得
                double speed = rs.getDouble("speed");
                double distance = rs.getDouble("distance");
                long timestamp = System.currentTimeMillis();
                
                data = speed + "," + distance + "," + timestamp;
            } else {
                // データがない場合はデフォルト値
                data = "0.0,0.0," + System.currentTimeMillis();
            }
            
            PrintWriter out = response.getWriter();
            out.print(data);
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("Error: " + e.getMessage());
        } finally {
            // リソースのクローズ
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}