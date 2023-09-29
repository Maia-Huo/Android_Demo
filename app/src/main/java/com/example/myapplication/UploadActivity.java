package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private TextView selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Button home = findViewById(R.id.navigation_home);
        Button upload = findViewById(R.id.navigation_upload);
        Button profile = findViewById(R.id.navigation_profile);

        selectedFileName = findViewById(R.id.selected_file_name);
        Button selectFileButton = findViewById(R.id.select_file_button);
        Button uploadButton = findViewById(R.id.upload_button);

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到home
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(UploadActivity.this, "Upload", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //跳转到profile
                Intent intent = new Intent(UploadActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动文件选择器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // 任何文件类型
                startActivityForResult(intent, PICK_FILE_REQUEST);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在此处执行上传文件的操作
                // 获取选定文件的 URI，并执行上传逻辑
                // 示例代码：uploadFile(selectedFileUri);
            }
        });
        // 创建 RequestPermission 实例
        new PermissionManager(this);




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // 处理选定的文件
            Uri selectedFileUri = data.getData();
            String selectedFileName = getFileNameFromUri(selectedFileUri);

            TextView selectedFileNameTextView = findViewById(R.id.selected_file_name);
            // 更新 TextView 的文本
            selectedFileNameTextView.setText("Selected File: " + selectedFileName);
        }
    }


    // 辅助方法：从 URI 获取文件名
    private String getFileNameFromUri(Uri uri) {
        // 根据 URI 查询文件名
        // 这里需要根据实际的 URI 处理方式来实现
        return "ExampleFileName.txt"; // 示例文件名
    }
}
