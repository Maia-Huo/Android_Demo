package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private GalleryAdapter galleryAdapter;
    private final List<PhotoItem> photoItemList = new ArrayList<>();


    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        // 初始化RecyclerView
        RecyclerView photoGallery = view.findViewById(R.id.photo_gallery);
        photoGallery.setLayoutManager(new LinearLayoutManager(getContext()));


        // 创建并设置适配器，传递初始空的数据列表
        galleryAdapter = new GalleryAdapter(photoItemList);
        photoGallery.setAdapter(galleryAdapter);

        // 在这里添加获取ViewModel的逻辑
        DataShare dataShare = new ViewModelProvider(requireActivity()).get(DataShare.class);

        dataShare.getPhotoItemList().observe(requireActivity(), photoItemList1 -> {
            Log.d("HomeFragment", "onChanged: " + photoItemList1.size());
            photoItemList.clear();
            // 更新全局变量的值
            for (int i = photoItemList1.size() - 1; i >= 0; i--) {
                photoItemList.add(photoItemList1.get(i));
            }
            galleryAdapter.notifyDataSetChanged(); // 通知适配器刷新数据
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> {
                // 重新获取数据
                dataShare.ReGet();
            }).start();
            swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }
}
