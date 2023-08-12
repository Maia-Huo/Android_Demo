//package com.example.myapplication;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.res.TypedArray;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MainActivity extends AppCompatActivity {
//
//    public static final String NEWS_ID = "news_id";
//    private List<News> newsList = new ArrayList<>();
//    private String[] titles;
//    private String[] authors;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initData();
//
//        NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this,
//                R.layout.list_item, newsList);
//
//        ListView lvNewsList = findViewById(R.id.lv_news_list);
//
//        lvNewsList.setAdapter(newsAdapter);
//    }
//
//    private void initData() {
//        int length;
//
//        titles = getResources().getStringArray(R.array.titles);
//        authors = getResources().getStringArray(R.array.authors);
//        TypedArray images = getResources().obtainTypedArray(R.array.images);
//
//        if (titles.length > authors.length) {
//            length = authors.length;
//        } else {
//            length = titles.length;
//        }
//
//        for (int i = 0; i < length; i++) {
//            News news = new News();
//            news.setTitle(titles[i]);
//            news.setAuthor(authors[i]);
//            news.setImageId(images.getResourceId(i, 0));
//
//            newsList.add(news);
//        }
//    }
//}

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String NEWS_ID = "news_id";
    private List<News> newsList = new ArrayList<>();
    private String[] titles;
    private String[] authors;
    private MySQLiteOpenHelper myDbHelper;
    private SQLiteDatabase db;
    private News news;
    private ListView lvNewsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvNewsList = findViewById(R.id.lvNewsList);

        myDbHelper = new MySQLiteOpenHelper(MainActivity.this);
        db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                NewsContract.NewsEntry.TABLE_NAME,
                null, null, null, null, null, null);

        List<News> newsList = new ArrayList<>();

        int titleIndex = cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_NAME_TITLE);
        int authorIndex = cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_NAME_AUTHOR);
        int imageIndex = cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_NAME_IMAGE);

        while (cursor.moveToNext()) {
            News news = new News();

            String title = cursor.getString(titleIndex);
            String author = cursor.getString(authorIndex);
            //String image = cursor.getString(imageIndex);
            String imageFileName = cursor.getString(imageIndex);

            Log.d("ImageTest", "Image File Name: " + imageFileName);
            int resourceId = getResources().getIdentifier(imageFileName, "drawable", getPackageName());
            Log.d("ImageTest", "Image Resource ID: " + resourceId);


            // 将资源ID解码成Bitmap对象
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            //Bitmap bitmap = BitmapFactory.decodeStream(
             //       getClass().getResourceAsStream("/" + image));

            news.setTitle(title);
            news.setAuthor(author);
            news.setImageId(bitmap);
            newsList.add(news);
        }

        NewsAdapter newsAdapter = new NewsAdapter(
                MainActivity.this,
                R.layout.list_item,
                newsList);
        lvNewsList.setAdapter(newsAdapter);
    }

}