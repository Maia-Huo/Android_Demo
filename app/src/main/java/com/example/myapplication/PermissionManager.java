package com.example.myapplication;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    public String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public PermissionManager(Activity activity) {
        requestPermissions(permissions,activity);
    }

    public void requestPermissions(String[] permissions,Activity activity) {
        // 检查是否有权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
            }
        }
    }
}