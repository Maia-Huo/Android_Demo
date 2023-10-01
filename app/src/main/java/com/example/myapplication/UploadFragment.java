package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;

public class UploadFragment extends Fragment {
    private byte[] imageBytes;
    private ActivityResultLauncher<String> mGetContent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_upload, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button selectFileButton = view.findViewById(R.id.select_file_button);
        Button uploadButton = view.findViewById(R.id.upload_button);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the returned Uri
                        if (uri != null) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(requireActivity().getContentResolver().openInputStream(uri));
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                imageBytes = stream.toByteArray();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动文件选择器
                // 创建并打开一个选择图片的Intent
                mGetContent.launch("image/*");
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        DatabaseConnectAndDataProcess databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                        Connection connection = databaseConnectAndDataProcess.Connect();
                        if (imageBytes != null && imageBytes.length > 0) {
                            boolean result = databaseConnectAndDataProcess.insert(connection, imageBytes);
                        }
                    }
                }).start();
            }
        });
    }
}