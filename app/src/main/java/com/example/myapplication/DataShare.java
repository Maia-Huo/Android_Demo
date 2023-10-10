package com.example.myapplication;
//ViewModel作用是将UI控制器与数据分离，使得UI控制器不需要关心数据的获取和处理，只需要关心数据的展示。

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataShare extends ViewModel {
    private MutableLiveData<DatabaseConnectAndDataProcess> databaseConnectAndDataProcessLiveData;
    private MutableLiveData<Connection> connectionMutableLiveData = new MutableLiveData<>();
    private Context context;

    public void SetContext(Context context) {
        this.context = context;
    }

    public LiveData<Connection> getConnection() {
        return connectionMutableLiveData;
    }

    public void setConnection(Connection connection) {
        connectionMutableLiveData.postValue(connection);
    }

    private MutableLiveData<List<PhotoItem>> photoItemListLiveData;
    private DatabaseConnectAndDataProcess databaseConnectAndDataProcess;
    private Connection connection;
    String username;

    public DataShare() {

        databaseConnectAndDataProcessLiveData = new MutableLiveData<>();
        photoItemListLiveData = new MutableLiveData<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                databaseConnectAndDataProcess = new DatabaseConnectAndDataProcess();
                connection = databaseConnectAndDataProcess.Connect();

                setDatabaseConnectAndDataProcess(databaseConnectAndDataProcess);
                setConnection(connection);

                //检查点赞状态
                ExamineCollect();
                //获取图片列表
                setPhotoItemList(databaseConnectAndDataProcess);

                NewPersonData();

            }
        };
        thread.start();

        //读取SharedPreferences中保存的账号
        SharedPreferences sharedPreferences = MainActivity_.Context.getSharedPreferences("username", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
    }

    public LiveData<DatabaseConnectAndDataProcess> getDatabaseConnectAndDataProcess() {
        return databaseConnectAndDataProcessLiveData;
    }

    public void setDatabaseConnectAndDataProcess(DatabaseConnectAndDataProcess databaseConnectAndDataProcess) {
        databaseConnectAndDataProcessLiveData.postValue(databaseConnectAndDataProcess);
    }

    public void NewData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                setPhotoItemList(databaseConnectAndDataProcess);
            }
        };
        thread.start();
    }

    private MutableLiveData<List<Bitmap>> bitmapsLiveData = new MutableLiveData<>();

    public void NewPersonData() {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread() {
            @Override
            public void run() {
                try {
                    List<Bitmap> bitmaps = databaseConnectAndDataProcess.PersonImageGet(connection, context, username);
                    handler.post(() -> {
                        bitmapsLiveData.postValue(bitmaps);
                    });
                } catch (SQLException | IOException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    public MutableLiveData<List<Bitmap>> getBitmaps() {
        return bitmapsLiveData;
    }

    public void setPhotoItemList(DatabaseConnectAndDataProcess databaseConnectAndDataProcess) {
        final Handler handler = new Handler(Looper.getMainLooper());
        try {
            List<PhotoItem> photoItems = databaseConnectAndDataProcess.ImageGet(connection, context);
            handler.post(() -> {
                photoItemListLiveData.postValue(photoItems);
            });
        } catch (SQLException | IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public MutableLiveData<List<PhotoItem>> getPhotoItemList() {
        return photoItemListLiveData; // 返回photoItemListLiveData对象，以便在UI中观察变化
    }

    public void ReGet() {
        setPhotoItemList(databaseConnectAndDataProcess);
    }

    public void updateLikes(int id, int newLikes) {
        new Thread() {
            @Override
            public void run() {
                databaseConnectAndDataProcess.updateLikes(connection, id, newLikes);
            }
        }.start();
    }


    private MutableLiveData<ArrayList<Integer>> collectLiveData = new MutableLiveData<>();

    public LiveData<ArrayList<Integer>> getCollect() {
        return collectLiveData;
    }

    ArrayList<Integer> collect1 = new ArrayList<>();

    public ArrayList<Integer> getCollectValue() {
        return collect1;
    }

    public void ExamineCollect() {
        final Handler handlers = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            ArrayList<Integer> collect = databaseConnectAndDataProcess.ExamineCollect(connection, username);
            Log.d("DataShare", "ExamineCollect: " + collect.size());
            handlers.post(() -> {
                collectLiveData.postValue(collect);
                collect1.addAll(collect);
            });
        }).start();
    }

    public boolean isCollect(int id) {
        return collect1.contains(id);
    }
    public void InsertCollect(int num) {
        new Thread() {
            @Override
            public void run() {
                databaseConnectAndDataProcess.InsertCollect(connection, username, num);
            }
        }.start();
    }

    public void DeleteCollect(int id) {
        new Thread() {
            @Override
            public void run() {
                databaseConnectAndDataProcess.DeleteCollect(connection, username, id);
            }
        }.start();
    }
}