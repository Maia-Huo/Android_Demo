package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String[] titles;
    private String[] authors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = getResources().getStringArray(R.array.titles);//获取字符串数组
        authors = getResources().getStringArray(R.array.authors);
        //船舰名为adapter的ArrayAdapter对象，用于与数组源（即titles数组）与列表视图进行绑定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, titles);

        ListView lvNewsList = findViewById(R.id.lv_news_list);
        lvNewsList.setAdapter(adapter);
    }
}