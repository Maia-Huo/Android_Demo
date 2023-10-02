package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView photoGallery;
    private GalleryAdapter galleryAdapter;
    private List<PhotoItem> photoItemList = new ArrayList<>();

    private void loadPhotosFromDatabase() {
        new Thread(new Runnable() {
            public void run() {
                DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                Connection connection = databaseConnectAndDataProcess.Connect();
                int len = databaseConnectAndDataProcess.Count(connection);
                try {
                    Bitmap[] bitmaps = databaseConnectAndDataProcess.ImageGet(connection);
                    if (bitmaps.length > 0) {
                        for (int i = 0; i < len; i++) {
                            Log.d("PhotoGalleryFragment", "run: " + bitmaps[i]);
                            final PhotoItem photoItem = new PhotoItem(bitmaps[i], "Author 1", 10);
                            if (isAdded()) { // Check if Fragment is still added to Activity
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        photoItemList.add(photoItem);
                                        galleryAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        // 设置背景
        view.setBackgroundColor(Color.WHITE); // 以白色背景为例
        // 其他初始化工作
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化RecyclerView
        photoGallery = view.findViewById(R.id.photo_gallery);
        photoGallery.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建并设置适配器，传递数据列表
        galleryAdapter = new GalleryAdapter(photoItemList);
        photoGallery.setAdapter(galleryAdapter);

        // 创建示例的图片项数据列表
        loadPhotosFromDatabase();
    }
}
