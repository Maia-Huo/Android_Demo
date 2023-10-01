package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Application;

public class DatabaseConnectAndDataProcess {
    //数据库
    final String url = "jdbc:mysql://rm-cn-lbj3es94l000h7jo.rwlb.rds.aliyuncs.com:3306/picture?autoReconnect=true&useSSL=false";
    final String user = "lly3323w";
    final String password = "Aa123456789";
    int count;
    private final Object lock = new Object();

    public Connection Connect() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url, user, password);
            // 连接到数据库成功，可以在这里执行SQL语句
            Log.d("sql", connection.getCatalog());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public int NumGet(Connection connection) {
        synchronized (lock) {
            String sql = "SELECT MAX(num) FROM images;";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return 0;
    }

    public int Count(Connection connection) {
        synchronized (lock) {
            String sql = "SELECT COUNT(num) FROM images;";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    this.count = resultSet.getInt(1);
                    return count;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    public boolean insert(Connection connection, byte[] imageBytes) {
        synchronized (lock){
            int num = NumGet(connection);
            String sql = "SELECT * FROM images WHERE num = ?";
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, num + 1);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return false;  // 数据已存在，不再插入
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            sql = "INSERT INTO images(image,num) VALUES (?,?)";
            statement = null;
            try {
                // 开始事务
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(sql);
                statement.setBytes(1, imageBytes);
                statement.setInt(2, num + 1);
                statement.executeUpdate();

                // 提交事务
                connection.commit();
            } catch (SQLException e) {
                // 回滚事务
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                throw new RuntimeException(e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // 释放连接
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public Bitmap[] ImageGet(Connection connection) throws SQLException {
        Bitmap[] bitmaps = new Bitmap[Count(connection)];
        String sql = "SELECT image FROM images;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        int i = 0;
        while (resultSet.next()) {
            Blob blob = resultSet.getBlob("image");
            InputStream inputStream = blob.getBinaryStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmaps[i] = bitmap;
            i++;
        }
        return bitmaps;
    }

    public int[] BitmapGet(Connection connection) {
        int[] nums = new int[Count(connection)];
        synchronized (lock) {
            String sql = "SELECT DISTINCT num FROM images;";
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                int i = 0;
                while (resultSet.next()) {
                    nums[i] = resultSet.getInt("num");
                    i++;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return nums;
    }

}
