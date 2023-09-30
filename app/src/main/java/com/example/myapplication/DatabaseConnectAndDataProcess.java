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
            String sql = "SELECT MAX(DISTINCT num) FROM images;";
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
            String sql = "SELECT COUNT(DISTINCT num) FROM images;";
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
        synchronized (lock) {
            int num = NumGet(connection);
            String sql = "INSERT INTO images(image,num) VALUES (?,?)";
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                statement.setBytes(1, imageBytes);
                statement.setInt(2, num + 1);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
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

    public Bitmap convertBlobsToBitmap(List<Blob> blobs, Context context) throws IOException, SQLException {
        // 创建一个临时文件
        File tempFile = File.createTempFile("temp_image", null, context.getCacheDir());
        tempFile.deleteOnExit();

        // 将 Blob 对象的数据写入临时文件
        FileOutputStream fos = new FileOutputStream(tempFile);
        for (Blob blob : blobs) {
            InputStream inputStream = blob.getBinaryStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        fos.close();

        // 获取临时文件的长度和宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;

        // 计算分块处理时每个块的高度
        int chunkSize = 1024; // 每个块的高度，可根据需要进行调整
        int numChunks = (height + chunkSize - 1) / chunkSize;

        // 分块处理文件，并将每个块转换成 Bitmap 对象
        Bitmap bitmap = null;
        for (int i = 0; i < numChunks; i++) {
            // 计算当前块的高度范围
            int startY = i * chunkSize;
            int endY = Math.min(startY + chunkSize, height);

            // 设置 BitmapFactory.Options 对象，只解码指定高度范围内的图像数据
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1; // 不进行缩放
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 设置颜色格式
            //         options.inMuteable = true; // 设置可变性，以便后续操作
            if (bitmap == null) {
                // 如果是第一个块，创建一个空的 Bitmap 对象
                bitmap = Bitmap.createBitmap(width, chunkSize, Bitmap.Config.ARGB_8888);
            } else {
                // 如果不是第一个块，创建一个与上一个块相同大小的 Bitmap 对象
                bitmap = Bitmap.createBitmap(width, chunkSize, bitmap.getConfig());
            }

            // 解码指定高度范围内的图像数据，并将其绘制到 Bitmap 对象上
            InputStream inputStream = new FileInputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                Log.d("bytesRead", String.valueOf(bytesRead));
                byte[] data = new byte[bytesRead];
                Log.d("data", String.valueOf(data.toString()));
                System.arraycopy(buffer, 0, data, 0, bytesRead);
                inputStream.skip(bytesRead); // 跳过已解码的数据
                Canvas canvas = new Canvas(bitmap);
                Bitmap chunkBitmap = BitmapFactory.decodeByteArray(data, 0, bytesRead, options);
                canvas.drawBitmap(chunkBitmap, 0, startY, null);
                chunkBitmap.recycle(); // 回收临时 Bitmap 对象
            }
            inputStream.close();
        }

        // 删除临时文件
        tempFile.delete();

        return bitmap;
    }
}
