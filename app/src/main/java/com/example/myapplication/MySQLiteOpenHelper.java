package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    //数据库版本
    private static final int DATABASE_VERSION = 1;
    //数据库名
    private static final String DATABASE_NAME = "news.db";

    //News表的创建SQL语句
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
                    NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY," +
                    NewsContract.NewsEntry.COLUMN_NAME_TITLE + " VARCHAR(200)," +
                    NewsContract.NewsEntry.COLUMN_NAME_AUTHOR + " VARCHAR(100)," +
                    NewsContract.NewsEntry.COLUMN_NAME_CONTENT + " TEXT," +
                    NewsContract.NewsEntry.COLUMN_NAME_IMAGE + " VARCHAR(100)" +
                    ")";

    //News表的删除SQL语句
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME;


    private Context mContext;

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //执行SQL语句，SQL_CREATE_ENTRIES就是上边创建表结构的SQL语句
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        //在表中插入初始数据，初始化数据库
        initDb(sqLiteDatabase);
    }

    private void initDb(SQLiteDatabase sqLiteDatabase) {
        //获取一个Resources对象，用于获取应用程序的资源，例如字符串数组、图像资源等
        Resources resources = mContext.getResources();
        String[] titles = resources.getStringArray(R.array.titles);
        String[] authors = resources.getStringArray(R.array.authors);
        String[] contents = resources.getStringArray(R.array.contents);
        TypedArray images = resources.obtainTypedArray(R.array.images);

        int length = 0;
        length = Math.min(titles.length,authors.length);
        length = Math.min(length,contents.length);
        length = Math.min(length,images.length());

        for (int i = 0; i < length; i++){
            //创建一个ContentValues对象，用于储存要插入数据库的数据
            ContentValues values = new ContentValues();
            values.put(NewsContract.NewsEntry.COLUMN_NAME_TITLE,
                    titles[i]);
            values.put(NewsContract.NewsEntry.COLUMN_NAME_AUTHOR,
                    authors[i]);
            values.put(NewsContract.NewsEntry.COLUMN_NAME_CONTENT,
                    contents[i]);

            //images.getString(i)将获取images资源TypedArray中第i个位置的字符串这个字符串是图像资源的名称
            //然后通过getIdentifier方法，根据图像资源名称和资源类型（这里是"drawable"），从应用程序的包名中获取资源ID
            int imageResourceId = resources.getIdentifier(
                    images.getString(i), "drawable", mContext.getPackageName());

            values.put(NewsContract.NewsEntry.COLUMN_NAME_IMAGE,
                    imageResourceId);

            //使用sqLiteDatabase对象的insert方法将准备好的数据插入到数据库表中
            //NewsContract.NewsEntry.TABLE_NAME是数据库表的名称，values是包含数据的ContentValues对象
            //该方法返回插入数据的行号，如果插入失败则返回-1
            long r =  sqLiteDatabase.insert(
                    NewsContract.NewsEntry.TABLE_NAME, null, values);
        }
    }

    //数据库版本更新时被调用的一个回调方法
    //onUpgrade方法的作用是在数据库版本更新时执行一系列操作，
    //包括删除现有的数据库表（通过 SQL_DELETE_ENTRIES 执行的 SQL 语句）
    //并重新创建新的数据库表（通过 onCreate(sqLiteDatabase) 方法）
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

}
