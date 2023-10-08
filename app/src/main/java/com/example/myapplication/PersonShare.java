package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PersonShare extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_share);

        DataShare dataShare = MainActivity_.dataShare;

        List<Bitmap> bitmaps = new ArrayList<>();
        // 更新全局变量的值
        dataShare.getBitmaps().observe(this, bitmaps::addAll);

        // 根据需要将bitmaps显示在相应的视图上
        RecyclerView recyclerView = findViewById(R.id.photo_person_share_gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // 设置RecyclerView的列数，根据你的需求而变化

        RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.person_photo, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ImageView imageView = holder.itemView.findViewById(R.id.person_photo_image);
                Bitmap bitmap = bitmaps.get(position);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public int getItemCount() {
                return bitmaps.size();
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}