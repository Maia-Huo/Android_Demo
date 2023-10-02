package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.sql.Connection;

public class MainActivity_ extends AppCompatActivity implements View.OnClickListener {

    private Button button1;
    private Button button2;
    private Button button3;
    private DataShare dataShare;

    private DatabaseConnectAndDataProcess databaseConnectAndDataProcess;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);



        // 绑定控件
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        dataShare = new ViewModelProvider(MainActivity_.this).get(DataShare.class);
        final boolean[] isrunning = {true};

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isrunning[0]) {
                    databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                    if (databaseConnectAndDataProcess != null) {
                        connection = databaseConnectAndDataProcess.Connect();
                        if (connection != null) {
                            isrunning[0] = false;
                            // 使用ViewModel的方法在主线程中更新值
                            dataShare.setDatabaseConnectAndDataProcess(databaseConnectAndDataProcess);
                            dataShare.setConnection(connection);
                        }
                    }
                }
            }
        }).start();
        // 创建并添加开局 Fragment 到布局中
        if(connection == null){
            replaceFragment(new HomeFragment());
        }
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