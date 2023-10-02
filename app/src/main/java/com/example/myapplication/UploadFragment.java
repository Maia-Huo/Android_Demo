package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;

public class UploadFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] imageBytes;
    private ImageView imagePreview;
    private EditText captionEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_upload, container, false);

        imagePreview = view.findViewById(R.id.image_preview);
        captionEditText = view.findViewById(R.id.caption_edit_text);
        Button selectImageButton = view.findViewById(R.id.select_image_button);
        Button postButton = view.findViewById(R.id.post_button);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postToFriendCircle();

            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
                imageBytes = getBytesFromBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
                // 显示发布按钮
                Button postButton = getView().findViewById(R.id.post_button);
                postButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void postToFriendCircle() {
        String caption = captionEditText.getText().toString();
        if (TextUtils.isEmpty(caption) && imageBytes == null) {
            Toast.makeText(getActivity(), "请输入文字或选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                synchronized (this) {
                    DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                    Connection connection = databaseConnectAndDataProcess.Connect();
                    if (imageBytes != null && imageBytes.length > 0) {
                        boolean result = databaseConnectAndDataProcess.insert(connection, imageBytes);
                    }
                }
            }
        }).start();

        // 在这里添加将文字和图片上传到朋友圈的逻辑
        // 您可以使用imageBytes和caption来执行上传操作

        // 清除文字和图片
        captionEditText.setText("");
        imagePreview.setImageResource(R.drawable.img_01);
        imagePreview.setVisibility(View.GONE);

        // 隐藏发布按钮
        Button postButton = getView().findViewById(R.id.post_button);
        postButton.setVisibility(View.GONE);

        Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
    }
}
