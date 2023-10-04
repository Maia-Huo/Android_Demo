package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        // 接收传递的数据

        // 获取传递的字节数组
        byte[] byteArray = getIntent().getByteArrayExtra("imageByteArray");
        // 将字节数组解码为位图
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        //Bitmap imageBitmap = getIntent().getParcelableExtra("imageBitmap");
        String author = getIntent().getStringExtra("author");
        String comment = getIntent().getStringExtra("comment");

        // 在布局中显示图像和相关信息
        ImageView imageView = findViewById(R.id.detail_image_view);
        TextView authorTextView = findViewById(R.id.detail_author_text);
        TextView commentTextView = findViewById(R.id.detail_comment_text);

        imageView.setImageBitmap(bitmap);
        authorTextView.setText(author);
        commentTextView.setText(comment);
    }
}
