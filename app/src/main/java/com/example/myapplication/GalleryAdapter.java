package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
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

        // 设置图片和其他UI元素
        holder.photoImageView.setImageBitmap(photoItem.getImageBitmap());
//        holder.photoImageView.setImageURI(photoItem.getImageUri());
        holder.authorTextView.setText(photoItem.getAuthor());
//        holder.likesTextView.setText(String.valueOf(photoItem.getLikes()));
        holder.comment.setText(photoItem.getComment());
        // 设置点赞按钮的状态
        if (photoItem.isLiked()) {
            holder.likeButton.setText("取消点赞");
        } else {
            holder.likeButton.setText("点赞");
        }

        // 设置点赞和评论按钮的点击事件
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理点赞操作
                photoItem.setLiked(!photoItem.isLiked());
                notifyDataSetChanged(); // 刷新适配器以更新UI
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
        }
    }
}
