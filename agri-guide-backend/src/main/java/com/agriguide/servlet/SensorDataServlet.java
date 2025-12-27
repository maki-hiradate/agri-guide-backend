package com.agriguide.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/sensor-data")
public class SensorDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // シンプルなテキスト形式で返す
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        // CORS設定（フロントエンドから呼び出すため）
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // データ（カンマ区切り）
        String data = "8.5,150.3," + System.currentTimeMillis();
        
        PrintWriter out = response.getWriter();
        out.print(data);
        out.flush();
    }
}