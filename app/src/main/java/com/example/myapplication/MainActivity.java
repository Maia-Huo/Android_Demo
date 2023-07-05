package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String NEWS_TITLE = "news_title";
    private static final String NEWS_AUTHOR = "news_author";

    /*使⽤ List<Map<String, String> > dataList; 替换掉之前定义的
    titles、authors 数组。将数据源的构造操作放⼊initData()⽅法中*/
    private List<Map<String, String>> dataList =  new ArrayList<>();
    private String[] titles;
    private String[] authors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this, dataList, android.R.layout.simple_list_item_2,
                new String[] { NEWS_TITLE, NEWS_AUTHOR },
                new int[] { android.R.id.text1, android.R.id.text2});

        ListView listView = findViewById(R.id.lv_news_list);
        listView.setAdapter(simpleAdapter);
    }

    private void initData() {
        int length;
        titles = getResources().getStringArray(R.array.titles);
        authors = getResources().getStringArray(R.array.authors);

        if(titles.length > authors.length) {
            length = authors.length;
        } else {
            length = titles.length;
        }

        for(int i = 0; i < length; i++) {
            Map map = new HashMap();
            map.put(NEWS_TITLE, titles[i]);
            map.put(NEWS_AUTHOR, authors[i]);
            dataList.add(map);
        }
    }
}