package com.example.myapplication;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PhotoItem {
    private Bitmap imageBitmap;
    private String author;
    private int likes;
    private boolean liked;
    private List<String> comments;

    public PhotoItem(Bitmap imageBitmap, String author, int likes) {
        this.imageBitmap = imageBitmap;
        this.author = author;
        this.likes = likes;
        this.liked = false;
        this.comments = new ArrayList<>();
    }


    public String getAuthor() {
        return author;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
