package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView photoGallery;
    private GalleryAdapter galleryAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<PhotoItem> photoItemList; // 存储图片项数据列表
    private int selectedPhotoPosition = -1; // 用于跟踪用户选择的图片位置

    public void initview() throws IOException {
        Button home = findViewById(R.id.navigation_home);
        Button upload = findViewById(R.id.navigation_upload);
        Button profile = findViewById(R.id.navigation_profile);

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到uppload
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到profile
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // 初始化RecyclerView
        photoGallery = findViewById(R.id.photo_gallery);
        photoGallery.setLayoutManager(new LinearLayoutManager(this));

// 创建示例的图片项数据列表
        List<PhotoItem> photoItemList = new ArrayList<>();
        new Thread(new Runnable() {
            public void run() {
                DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                Connection connection = databaseConnectAndDataProcess.Connect();
                int len = databaseConnectAndDataProcess.Count(connection);
                try {
                    Bitmap[] bitmaps = databaseConnectAndDataProcess.ImageGet(connection);
                    if (bitmaps.length > 0) {
                        for (int i = 0; i < len; i++) {
                            Log.d("MainActivity", "run: " + bitmaps[i]);
                            final PhotoItem photoItem = new PhotoItem(bitmaps[i], "Author 1", 10);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    photoItemList.add(photoItem);
                                    galleryAdapter.notifyDataSetChanged(); // 通知适配器数据改变
                                }
                            });
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
// 创建并设置适配器，传递数据列表
        galleryAdapter = new GalleryAdapter(photoItemList);
        photoGallery.setAdapter(galleryAdapter);

        Log.d("MainActivity", "initview: " + photoItemList.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new PermissionManager(this);
        try {
            initview();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
