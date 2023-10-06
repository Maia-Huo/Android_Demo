package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;

public class UploadFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] imageBytes;
    private ImageView imagePreview;
    private EditText captionEditText;

    private DataShare dataShare;

    private String username;
    private DatabaseConnectAndDataProcess databaseConnectAndDataProcess;
    private Connection connection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_upload, container, false);
        // 在这里添加获取ViewModel的逻辑
        dataShare = new ViewModelProvider(requireActivity()).get(DataShare.class);

        //获取用户名
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("username", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        //绑定控件
        imagePreview = view.findViewById(R.id.image_preview);
        captionEditText = view.findViewById(R.id.comment);
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, stream);
        return stream.toByteArray();
    }

    private void postToFriendCircle() {
        // 观察 ViewModel 中的数据变化
        dataShare.getDatabaseConnectAndDataProcess().observe(requireActivity(), new Observer<DatabaseConnectAndDataProcess>() {
            @Override
            public void onChanged(DatabaseConnectAndDataProcess databaseConnectAndDataProcess1) {
                // 更新全局变量的值
                databaseConnectAndDataProcess = databaseConnectAndDataProcess1;
                // 更新 UI 或执行其他操作
            }
        });

        dataShare.getConnection().observe(requireActivity(), new Observer<Connection>() {
            @Override
            public void onChanged(Connection connection1) {
                // 更新全局变量的值
                connection = connection1;
                // 更新 UI 或执行其他操作
            }
        });


        //Log.d("username", username);
        String caption = captionEditText.getText().toString();
        if (TextUtils.isEmpty(caption) && imageBytes == null) {
            Toast.makeText(getActivity(), "请输入文字或选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        String comment = captionEditText.getText().toString();//获取输入的评论


        new Thread(new Runnable() {
            public void run() {
                synchronized (this) {
                    if (imageBytes != null && imageBytes.length > 0) {
                        databaseConnectAndDataProcess.insert(connection, imageBytes, comment, username, 0);
                        dataShare.ReGet();
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
