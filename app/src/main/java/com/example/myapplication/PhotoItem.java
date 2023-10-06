package com.example.myapplication;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PhotoItem {
    private int id;
    private Bitmap imageBitmap;
    private String author;
    private String comment;
    private int likes;
    private boolean liked;
    private List<String> comments;

    public PhotoItem(Bitmap imageBitmap, String author,String comment,int likes,int id) {
        this.imageBitmap = imageBitmap;
        this.author = author;
        this.comment = comment;
        this.likes = likes;
        this.liked = false;
        this.comments = new ArrayList<>();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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
