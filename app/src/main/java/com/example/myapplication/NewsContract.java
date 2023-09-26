package com.example.myapplication;

import android.provider.BaseColumns;

public class NewsContract {
    private NewsContract() {}

    //BaseColumns接口中默认提供了一个名为"_ID"的字段，用于在数据库中表示每行数据的唯一ID
    public static class NewsEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "news";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}
