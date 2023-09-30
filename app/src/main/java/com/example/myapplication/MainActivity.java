package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.sql.Connection;
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
        // 初始化RecyclerView
        photoGallery = findViewById(R.id.photo_gallery);
        photoGallery.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Bitmap> bitmaps_= new ArrayList<>();
        // 创建示例的图片项数据列表
        List<PhotoItem> photoItemList = new ArrayList<>();
        new Thread(new Runnable() {
            public void run() {
                DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                Connection connection = databaseConnectAndDataProcess.Connect();
                ArrayList<Bitmap> bitmaps = databaseConnectAndDataProcess.ImageGet(connection,MainActivity.this);
                for(Bitmap bitmap:bitmaps){
                    bitmaps_.add(bitmap);
                }
   //             photoItemList.add(new PhotoItem(bitmaps.get(0), "Author 1", 10));
            }
        }).start();
        // 添加更多图片项...

        if(bitmaps_.size() == 0){
            Toast.makeText(MainActivity.this, "No image", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.this, "Get image", Toast.LENGTH_SHORT).show();
        }
        // 创建并设置适配器，传递数据列表
        galleryAdapter = new GalleryAdapter(photoItemList);
        photoGallery.setAdapter(galleryAdapter);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            // 获取选择的图片的URI
//            Uri selectedImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.img_01); // 用于测试，您可以替换为实际的URI
//
//
//            // 创建新的图片项并添加到列表
//            PhotoItem newPhotoItem = new PhotoItem(selectedImageUri, "New Author", 0); // 0表示初始点赞数
//            photoItemList.add(0, newPhotoItem); // 添加到列表顶部
//            galleryAdapter.notifyItemInserted(0); // 刷新适配器以更新UI
//        }
//    }


}
