package com.example.myapplication;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    public String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
        requestPermissions(permissions);
    }

    public void requestPermissions(String[] permissions) {
        // 检查是否有权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
            }
        }
    }

//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        // 处理权限请求的结果
//        switch (requestCode) {
//            case 1:
//                for (int i = 0; i < permissions.length; i++) {
//                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        // 用户已经授予了所需的权限，你可以在这里执行相应的操作
//                    } else {
//                        // 用户已经拒绝了所需的权限，你可以在这里执行相应的操作
//                    }
//                }
//                break;
//        }
//    }
}