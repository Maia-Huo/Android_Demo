package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashScreenActivity extends AppCompatActivity {
    private DatabaseConnectAndDataProcess databaseConnectAndDataProcess;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        PermissionManager permissionManager = new PermissionManager(this);
        // 初始化SharedPreferences
        SharedPreferences spFile = getSharedPreferences(
                getResources().getString(R.string.shared_preferences_file_name),
                Context.MODE_PRIVATE);

        // 从SharedPreferences中获取用户已登录的标志
        boolean isUserLoggedIn = spFile.getBoolean("isUserLoggedIn", false);

        Intent intent;
        if (isUserLoggedIn) {
            // 用户已登录，跳转到主页面
            intent = new Intent(getInstance(), MainActivity_.class);
        } else {
            // 用户未登录，跳转到登录页面
            intent = new Intent(getInstance(), LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public SplashScreenActivity getInstance() {
        return this;
    }
}
