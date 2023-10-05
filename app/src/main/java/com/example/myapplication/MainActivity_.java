package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity_ extends AppCompatActivity implements View.OnClickListener {
    private DataShare dataShare;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // 绑定控件
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

//        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            public void onRefresh() {
//                // 开始刷新
//                dataShare.NewData();
//                replaceFragment(new HomeFragment());
//                // 通知刷新结束
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
        replaceFragment(new HomeFragment()); // Move this line here.

    }

    public DataShare getDataShare() {
        return dataShare;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            replaceFragment(new UploadFragment());
        } else if (v.getId() == R.id.button2) {
            replaceFragment(new HomeFragment());
        } else if (v.getId() == R.id.button3) {
            replaceFragment(new ProfileFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
