package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //private BottomNavigationView bottomNavigationView;
    private RecyclerView photoGallery;
    private GalleryAdapter galleryAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<PhotoItem> photoItemList; // 存储图片项数据列表
    private int selectedPhotoPosition = -1; // 用于跟踪用户选择的图片位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment selectedFragment = null;
//
////                switch (item.getItemId()) {
////                    case R.id.navigation_home:
////                        selectedFragment = new HomeFragment();
////                        break;
////                    case R.id.navigation_upload:
////                        selectedFragment = new UploadFragment();
////                        break;
////                    case R.id.navigation_profile:
////                        selectedFragment = new ProfileFragment();
////                        break;
////                }
//
//                if (item.getItemId() == R.id.navigation_home) {
//                    selectedFragment = new HomeFragment();
//                } else if (item.getItemId() == R.id.navigation_upload) {
//                    selectedFragment = new UploadFragment();
//                } else if (item.getItemId() == R.id.navigation_profile) {
//                    selectedFragment = new ProfileFragment();
//                }
//
//
//                if (selectedFragment != null) {
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
//                }
//
//                return true;
//            }
//        });

        // 默认选择主页
        //bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // 初始化RecyclerView
        photoGallery = findViewById(R.id.photo_gallery);
        photoGallery.setLayoutManager(new LinearLayoutManager(this));

        // 创建示例的图片项数据列表
        List<PhotoItem> photoItemList = new ArrayList<>();
        photoItemList.add(new PhotoItem(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.img_01), "Author 1", 10));
        photoItemList.add(new PhotoItem(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.img_02), "Author 2", 15));

        // 添加更多图片项...

        // 创建并设置适配器，传递数据列表
        galleryAdapter = new GalleryAdapter(photoItemList);
        photoGallery.setAdapter(galleryAdapter);

//        // 上传图片按钮点击事件
//        Button uploadButton = findViewById(R.id.upload_button);
//        uploadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 启动图片选择器
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
//            }
//        });

        // 添加点赞和评论功能的代码将在下面的步骤中添加
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // 获取选择的图片的URI
            Uri selectedImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.img_01); // 用于测试，您可以替换为实际的URI


            // 创建新的图片项并添加到列表
            PhotoItem newPhotoItem = new PhotoItem(selectedImageUri, "New Author", 0); // 0表示初始点赞数
            photoItemList.add(0, newPhotoItem); // 添加到列表顶部
            galleryAdapter.notifyItemInserted(0); // 刷新适配器以更新UI
        }
    }



}
