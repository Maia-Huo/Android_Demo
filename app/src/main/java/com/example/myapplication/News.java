package com.example.myapplication;

import android.graphics.Bitmap;

public class News {
    private String mTitle;
    private String mAuthor;
    private String mContent;
    private Bitmap image;

    private int imageResourceId;
    private int mImageId;

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getContent(){
        return mContent;
    }

    public int getImageId(){
        return mImageId;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public void setAuthor(String author){
        this.mAuthor = author;
    }

    public void setContent(String content){
        this.mContent = content;
    }

    public void setImageId(int imageId){
        this.mImageId = imageId;
    }

    public void setImageId(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
