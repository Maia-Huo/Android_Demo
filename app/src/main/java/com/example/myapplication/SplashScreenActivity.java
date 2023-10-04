package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // 初始化SharedPreferences
        SharedPreferences spFile = getSharedPreferences(
                getResources().getString(R.string.shared_preferences_file_name),
                Context.MODE_PRIVATE);

        // 从SharedPreferences中获取用户已登录的标志
        boolean isUserLoggedIn = spFile.getBoolean("isUserLoggedIn", false);

        Intent intent;
        if (isUserLoggedIn) {
            // 用户已登录，跳转到主页面
            intent = new Intent(this, MainActivity_.class);
        } else {
            // 用户未登录，跳转到登录页面
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
