package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bumptech.glide.Glide;

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
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DatabaseConnectAndDataProcess {
    //数据库
    final String url = "jdbc:mysql://rm-cn-lbj3es94l000h7jo.rwlb.rds.aliyuncs.com:3306/picture?autoReconnect=true&useSSL=false";
    final String user = "lly3323w";
    final String password = "Aa123456789";
    int count;
    String[] username;
    String[] comment;

    int[] likes;
    int[] num;
    private final Object lock = new Object();

    public Connection Connect() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url, user, password);
            // 连接到数据库成功，可以在这里执行SQL语句
            Log.d("sql", "连接成功");
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

    public boolean insert(Connection connection, byte[] imageBytes, String comment, String username, int likes) {
        synchronized (lock) {
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

            sql = "INSERT INTO images(image,num,comment,username,likes) VALUES (?,?,?,?,?)";
            statement = null;
            try {
                // 开始事务
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(sql);
                statement.setBytes(1, imageBytes);
                statement.setInt(2, num + 1);
                statement.setString(3, comment);
                statement.setString(4, username);
                statement.setInt(5, likes);
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


    public boolean updateLikes(Connection connection, int num, int newLikes) {
        synchronized (lock) {
            String sql = "UPDATE images SET likes = ? WHERE num = ?";
            PreparedStatement statement = null;
            try {
                // 开始事务
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(sql);
                statement.setInt(1, newLikes);
                statement.setInt(2, num);
                statement.executeUpdate();

                // 提交事务
                connection.commit();
                return true;
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
    }


    public List<PhotoItem> ImageGet(Connection connection, Context context) throws SQLException, IOException, ExecutionException, InterruptedException {
        List<PhotoItem> photoItems = new ArrayList<>();
        String sql = "SELECT image, username, comment,likes,num FROM images;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            // 获取图片
            Blob blob = resultSet.getBlob("image");
            if (blob != null) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                if (bytes != null) {
                    // 使用Glide加载Bitmap
                    Bitmap bitmap = blobToBitmap(context, blob);
                    String username = resultSet.getString("username");
                    String comment = resultSet.getString("comment");
                    int likes = resultSet.getInt("likes");
                    int num = resultSet.getInt("num");
                    PhotoItem photoItem = new PhotoItem(bitmap, username, comment, likes, num);
                    photoItems.add(photoItem);
                }
            }
        }
        return photoItems;
    }

    public Bitmap blobToBitmap(Context context, Blob blob) throws IOException, InterruptedException, ExecutionException, SQLException {
        if (blob != null) {
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            if (bytes != null) {
                return Glide.with(context)
                        .asBitmap()
                        .load(bytes)
                        .submit()
                        .get();
            }
        }
        return null;
    }

    public List<Bitmap> PersonImageGet(Connection connection, Context context, String username) throws SQLException, IOException, ExecutionException, InterruptedException {
        List<Bitmap> bitmaps = new ArrayList<>();
        String sql = "SELECT image FROM images where username = " + username + ";";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            // 获取图片
            Blob blob = resultSet.getBlob("image");
            if (blob != null) {
                Bitmap bitmap = blobToBitmap(context, blob);
                if (bitmap != null) {
                    bitmaps.add(bitmap);
                }
            }
        }
        return bitmaps;
    }

    public int[] NumGet() {
        return num;
    }

    public String[] CommentGet() {
        return comment;
    }

    public String[] UsernameGet() {
        return username;
    }

    public int[] LikesGet() {
        return likes;
    }

    public void UsernameSet(String[] username) {
        this.username = username;
    }

    public void CommentSet(String[] comment) {
        this.comment = comment;
    }

    public void LikesSet(int[] likes) {
        this.likes = likes;
    }


    public ArrayList<Integer> ExamineCollect(Connection connection, String user) {
        ArrayList<Integer> collect = new ArrayList<>();
        synchronized (lock) {
            // 查询是否已存在(防止重复插入)
            String sql = "SELECT picture_num FROM collect WHERE user = ?;";
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                statement.setString(1, user);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    collect.add(resultSet.getInt("picture_num"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return collect;
    }


    public boolean InsertCollect(Connection connection, String user, int num) {
        synchronized (lock) {

            PreparedStatement statement = null;
            String sql = "INSERT INTO collect(user,picture_num,sign) VALUES (?,?,?)";
            statement = null;
            try {
                // 开始事务
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(sql);
                statement.setString(1, user);
                statement.setInt(2, num);
                statement.setString(3, user + num);
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

    public void DeleteCollect(Connection connection, String username, int id) {
        synchronized (lock) {
            String sql = "DELETE FROM collect WHERE user = ? AND picture_num = ?";
            PreparedStatement statement = null;
            try {
                // 开始事务
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setInt(2, id);
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
    }
}
