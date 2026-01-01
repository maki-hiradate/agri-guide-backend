package com.agriguide.api;

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

/**
 * 走行履歴データを取得するServlet（Jakarta EE 10対応・改善版CORS）
 */
@WebServlet("/api/history-data")
public class HistoryDataServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agriguide_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root1234";
    
    /**
     * CORS設定（動的Origin対応）
     */
    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        
        // 開発中はlocalhostと127.0.0.1のどちらも許可
        if (origin != null && 
            (origin.equals("http://localhost:8000") || origin.equals("http://127.0.0.1:8000"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
    
    /**
     * OPTIONSリクエスト（プリフライト）に対応
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS設定
        setCorsHeaders(request, response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // MySQLドライバをロード
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // データベース接続
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // SQL: 最新10件を取得
            String sql = "SELECT id, speed, distance, latitude, longitude, timestamp " +
                        "FROM trip_data " +
                        "ORDER BY timestamp DESC " +
                        "LIMIT 10";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            // 手動でJSONを構築
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                first = false;
                
                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"speed\":").append(rs.getDouble("speed")).append(",");
                json.append("\"distance\":").append(rs.getDouble("distance")).append(",");
                json.append("\"latitude\":").append(rs.getDouble("latitude")).append(",");
                json.append("\"longitude\":").append(rs.getDouble("longitude")).append(",");
                json.append("\"timestamp\":\"").append(rs.getTimestamp("timestamp")).append("\"");
                json.append("}");
            }
            
            json.append("]");
            
            // JSONを返す
            out.print(json.toString());
            
            // リソースを閉じる
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // エラーレスポンス
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
