package com.example.android.booklistingapp;

/**
 * Created by sr on 09.07.17.
 */

public class Book {
    private String mTitle;
    private String mAuthor;
    private String mUrl;

    //constructor

    public Book(String title, String author, String url) {
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    //get methods

    public String getTitle(){
        return mTitle;
    }

    public String getAuthors(){
        return mAuthor;
    }

    public String getUrl(){
        return mUrl;
    }

}
