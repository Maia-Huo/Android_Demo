package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Arrays;

public class UploadActivity extends AppCompatActivity {

    private TextView selectedFileName;
    byte[] imageBytes;

    // 创建 RequestPermission 实例
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        imageBytes = stream.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


    public void initview() {
        Button home = findViewById(R.id.navigation_home);
        Button upload = findViewById(R.id.navigation_upload);
        Button profile = findViewById(R.id.navigation_profile);

        selectedFileName = findViewById(R.id.selected_file_name);
        Button selectFileButton = findViewById(R.id.select_file_button);
        Button uploadButton = findViewById(R.id.upload_button);

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到home
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(UploadActivity.this, "Upload", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到profile
                Intent intent = new Intent(UploadActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动文件选择器
                // 创建并打开一个选择图片的Intent
                mGetContent.launch("image/*");
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                        Connection connection = databaseConnectAndDataProcess.Connect();
                        boolean result = databaseConnectAndDataProcess.insert(connection,imageBytes);
                    }
                }).start();
            }
        });
        // 创建 RequestPermission 实例
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        new PermissionManager(this);
        initview();
    }

    // 辅助方法：从 URI 获取文件名
    private String getFileNameFromUri(Uri uri) {
        // 根据 URI 查询文件名
        // 这里需要根据实际的 URI 处理方式来实现
        return "ExampleFileName.txt"; // 示例文件名
    }
}
