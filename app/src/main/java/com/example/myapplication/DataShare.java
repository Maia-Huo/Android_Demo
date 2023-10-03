package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.sql.Connection;

public class DataShare extends ViewModel {
    private MutableLiveData<DatabaseConnectAndDataProcess> databaseConnectAndDataProcess;
    private MutableLiveData<Connection> connection;
    private MutableLiveData<String[]> username;

    public DataShare() {
        // 初始化全局变量
        databaseConnectAndDataProcess = new MutableLiveData<>();
        connection = new MutableLiveData<>();
        username = new MutableLiveData<>();
    }

    public LiveData<DatabaseConnectAndDataProcess> getDatabaseConnectAndDataProcess() {
        return databaseConnectAndDataProcess;
    }

    public void setDatabaseConnectAndDataProcess(DatabaseConnectAndDataProcess databaseConnectAndDataProcess) {
        this.databaseConnectAndDataProcess.postValue(databaseConnectAndDataProcess);
    }

    public LiveData<Connection> getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection.postValue(connection);
    }

    public LiveData<String[]> getUsername() {
        return username;
    }

    public void setUsername(String[] username) {
        this.username.postValue(username);
    }
}