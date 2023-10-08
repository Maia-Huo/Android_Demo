package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<PhotoItem> photoList;

    public GalleryAdapter(List<PhotoItem> photoList) {
        this.photoList = photoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PhotoItem photoItem = photoList.get(position);

        //int likes = photoItem.getLikes();
        // 设置点赞按钮的状态
        if (photoItem.isLiked()) {
            holder.likeButton.setText("取消点赞");
            //likes += 1;
        } else {
            holder.likeButton.setText("点赞");
        }

        // 设置图片和其他UI元素
        holder.photoImageView.setImageBitmap(photoItem.getImageBitmap());
//        holder.photoImageView.setImageURI(photoItem.getImageUri());
        holder.authorTextView.setText("作者：" + photoItem.getAuthor());
        //holder.likesTextView.setText(String.valueOf(likes));
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

                // 将所选图像项的数据传递给新的活动
                //intent.putExtra("imageBitmap", photoItem.getImageBitmap());

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

                int newLikes = photoItem.isLiked() ? photoItem.getLikes() + 1 : photoItem.getLikes() - 1;

                //updateLikes(photoItem, newLikes); // 更新点赞数
                photoItem.setLikes(newLikes);
                notifyDataSetChanged(); // 刷新适配器以更新UI);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                        Connection connection = databaseConnectAndDataProcess.Connect();

                        databaseConnectAndDataProcess.updateLikes(connection, photoItem.getId(), newLikes);
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("likes", 0);
                sharedPreferences.edit().putInt("likes", newLikes).apply();
                sharedPreferences.edit().putInt("num", photoItem.getId()).apply();
            }
        });

        // 显示评论列表
        StringBuilder commentsText = new StringBuilder();
        List<String> comments = photoItem.getComments();
        for (String comment : comments) {
            commentsText.append(comment).append("\n");
        }
        holder.commentsTextView.setText(commentsText.toString());

        // 设置评论按钮的点击事件
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理评论操作
                String commentText = holder.commentEditText.getText().toString().trim();

                // 如果评论文本不为空，将评论添加到 PhotoItem 对象中
                if (!commentText.isEmpty()) {
                    photoItem.addComment(commentText);
                    notifyDataSetChanged(); // 刷新适配器以更新UI
                    holder.commentEditText.setText(""); // 清空评论输入框
                }
            }
        });
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
        public Button commentButton;
        public TextView commentsTextView;
        public EditText commentEditText;
        public Button download;

        public ViewHolder(View view) {
            super(view);
            photoImageView = view.findViewById(R.id.photo_image);
            authorTextView = view.findViewById(R.id.author_text);
            comment = view.findViewById(R.id.comment);
            likesTextView = view.findViewById(R.id.likes_count);
            likeButton = view.findViewById(R.id.like_button);
            commentButton = view.findViewById(R.id.comment_button);
            commentsTextView = view.findViewById(R.id.comments_text);
            commentEditText = view.findViewById(R.id.comment_edit_text);
            download = view.findViewById(R.id.download);
        }
    }
}
