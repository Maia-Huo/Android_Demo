package com.example.myapplication;
//ViewModel作用是将UI控制器与数据分离，使得UI控制器不需要关心数据的获取和处理，只需要关心数据的展示。

import android.content.Context;
import android.content.Intent;
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

public class DataShare extends ViewModel {
    private MutableLiveData<DatabaseConnectAndDataProcess> databaseConnectAndDataProcessLiveData;
    private MutableLiveData<Connection> connectionMutableLiveData = new MutableLiveData<>();

    public LiveData<Connection> getConnection() {
        return connectionMutableLiveData;
    }

    public void setConnection(Connection connection) {
        connectionMutableLiveData.postValue(connection);
    }

    private MutableLiveData<List<PhotoItem>> photoItemListLiveData;
    private DatabaseConnectAndDataProcess databaseConnectAndDataProcess;
    private Connection connection;
    private Bitmap[] bitmaps;

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
                setPhotoItemList(databaseConnectAndDataProcess);
            }
        };
        thread.start();
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

    public void setPhotoItemList(DatabaseConnectAndDataProcess databaseConnectAndDataProcess) {
        final Handler handler = new Handler(Looper.getMainLooper());
        try {
            bitmaps = databaseConnectAndDataProcess.ImageGet(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] usernames = databaseConnectAndDataProcess.UsernameGet();
        String[] comments = databaseConnectAndDataProcess.CommentGet();
        int[] likes = databaseConnectAndDataProcess.LikesGet();
        int[] num = databaseConnectAndDataProcess.NumGet();


        if (bitmaps.length > 0) {
            handler.post(() -> {
                List<PhotoItem> photoItems = new ArrayList<>();
                for (int i = bitmaps.length - 1; i >= 0; i--) {
                    final PhotoItem photoItem = new PhotoItem(bitmaps[i], usernames[i], comments[i], likes[i], num[i]);
                    photoItems.add(photoItem);
                }
                photoItemListLiveData.postValue(photoItems);
            });
        }
    }

    public Bitmap getBitmap() {
        return bitmaps[0];
    }

    public MutableLiveData<List<PhotoItem>> getPhotoItemList() {
        return photoItemListLiveData; // 返回photoItemListLiveData对象，以便在UI中观察变化
    }

    public void ReGet() {
        setPhotoItemList(databaseConnectAndDataProcess);
    }


}