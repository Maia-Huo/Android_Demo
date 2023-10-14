package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<PhotoItem> photoList;
    private DataShare dataShare;

    public GalleryAdapter(List<PhotoItem> photoList) {
        this.photoList = photoList;
        dataShare = MainActivity_.dataShare;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PhotoItem photoItem = photoList.get(position);

        // 设置图片和其他UI元素
        holder.photoImageView.setImageBitmap(photoItem.getImageBitmap());
        holder.authorTextView.setText("作者：" + photoItem.getAuthor());
        holder.likesTextView.setText(String.valueOf(photoItem.getLikes()));
        holder.comment.setText(photoItem.getComment());
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        SaveUtils.saveBitmapToAlbum(MainActivity_.Context, photoItem.getImageBitmap());
                    }
                }).start();
            }
        });
        // 设置点击事件
        holder.photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前点击项的 PhotoItem
                PhotoItem photoItem = photoList.get(holder.getAdapterPosition());

                // 创建一个Intent来启动新的活动（图片详情页面）
                Intent intent = new Intent(v.getContext(), DisplayImage.class);

                // 将位图压缩为字节数组
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photoItem.getImageBitmap().compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("imageByteArray", byteArray);

                intent.putExtra("author", photoItem.getAuthor());
                intent.putExtra("comment", photoItem.getComment());

                // 启动新的活动
                v.getContext().startActivity(intent);
            }
        });


        // 设置点赞和评论按钮的点击事件

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理点赞操作
                photoItem.setLiked(!photoItem.isLiked());

                // 更新点赞数
                int newLikes = photoItem.isLiked() ? photoItem.getLikes() + 1 : photoItem.getLikes() - 1;

                photoItem.setLikes(newLikes);

                // 更新数据库点赞状态
                if (photoItem.isLiked()) {
                    dataShare.InsertCollect(photoItem.getId());
                } else {
                    dataShare.DeleteCollect(photoItem.getId());
                }

                // 更新数据库中的点赞数
                dataShare.updateLikes(photoItem.getId(), newLikes);
                notifyDataSetChanged();
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("likes", 0);
                sharedPreferences.edit().putInt("likes", newLikes).apply();
                sharedPreferences.edit().putInt("num", photoItem.getId()).apply();

            }
        });

        if (photoItem.isLiked()) {
            holder.likeButton.setText("取消点赞");
        } else {
            holder.likeButton.setText("点赞");
        }

        // 显示评论列表
        StringBuilder commentsText = new StringBuilder();
        List<String> comments = photoItem.getComments();
        for (String comment : comments) {
            commentsText.append(comment).append("\n");
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;
        public TextView authorTextView;
        public TextView comment;
        public TextView likesTextView;
        public Button likeButton;
        public Button download;

        public ViewHolder(View view) {
            super(view);
            photoImageView = view.findViewById(R.id.photo_image);
            authorTextView = view.findViewById(R.id.author_text);
            comment = view.findViewById(R.id.comment);
            likesTextView = view.findViewById(R.id.likes_count);
            likeButton = view.findViewById(R.id.like_button);
            download = view.findViewById(R.id.download);
        }
    }
}
