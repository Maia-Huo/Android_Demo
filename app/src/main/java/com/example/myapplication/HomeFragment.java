package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeFragment extends Fragment {
    private RecyclerView photoGallery;
    private GalleryAdapter galleryAdapter;
    private List<PhotoItem> photoItemList = new ArrayList<>();

    private DatabaseConnectAndDataProcess databaseConnectAndDataProcess;
    private Connection connection;
    private String username;
    private DataShare dataShare;
    private boolean isUploadCompleted = false;

    private void loadPhotosFromDatabase() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Void> future = executorService.submit(() -> {
            int len = databaseConnectAndDataProcess.Count(connection);
            try {
                Bitmap[] bitmaps = databaseConnectAndDataProcess.ImageGet(connection);
                String[] usernames = databaseConnectAndDataProcess.UsernameGet();
                String[] comments = databaseConnectAndDataProcess.CommentGet();
                if (bitmaps.length > 0) {
                    for (int i = 0; i < len; i++) {
                        Log.d("PhotoGalleryFragment", "doInBackground: " + bitmaps[i]);
                        final PhotoItem photoItem = new PhotoItem(bitmaps[i], usernames[i], comments[i]);
                        if (isAdded()) { // Check if Fragment is still added to Activity
                            requireActivity().runOnUiThread(() -> {
                                photoItemList.add(photoItem);
                                if (photoItemList.size() == len) { // 所有图片都已加载完成
                                    galleryAdapter.notifyDataSetChanged();
                                    // 隐藏加载指示符
                                }
                            });
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });

        executorService.shutdown(); // 关闭ExecutorService

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isUploadCompleted) {
                // 数据还没有完全同步到数据库，不允许再次上传数据
                return;
            }
            // 数据已经完全同步到数据库，允许再次上传数据
            loadPhotosFromDatabase();
        }, 1000);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);


        // 在这里添加获取ViewModel的逻辑
        dataShare = new ViewModelProvider(requireActivity()).get(DataShare.class);
        // 观察 ViewModel 中的数据变化
        dataShare.getDatabaseConnectAndDataProcess().observe(requireActivity(), new Observer<DatabaseConnectAndDataProcess>() {
            @Override
            public void onChanged(DatabaseConnectAndDataProcess databaseConnectAndDataProcess1) {
                // 更新全局变量的值
                databaseConnectAndDataProcess = databaseConnectAndDataProcess1;
                Log.d("sql", "授予成功3");
                // 更新 UI 或执行其他操作
            }
        });
        dataShare.getConnection().observe(requireActivity(), new Observer<Connection>() {
            @Override
            public void onChanged(Connection connection1) {
                // 更新全局变量的值
                connection = connection1;
                Log.d("sql", "授予成功4");
                // 更新 UI 或执行其他操作
            }
        });
        // 设置背景
        view.setBackgroundColor(Color.WHITE); // 以白色背景为例
        // 其他初始化工作
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
// 创建示例的图片项数据列表
        loadPhotosFromDatabase();
// 初始化RecyclerView
        photoGallery = view.findViewById(R.id.photo_gallery);
        photoGallery.setLayoutManager(new LinearLayoutManager(getContext()));

// 创建并设置适配器，传递数据列表
        galleryAdapter = new GalleryAdapter(photoItemList);
        photoGallery.setAdapter(galleryAdapter);

// 检查数据是否已加载完成，如果已完成则隐藏加载指示符并显示RecyclerView，否则显示加载指示符
        if (photoItemList.isEmpty()) {
            // 显示加载指示符
        } else {
            // 隐藏加载指示符并显示RecyclerView
        }
    }
}
