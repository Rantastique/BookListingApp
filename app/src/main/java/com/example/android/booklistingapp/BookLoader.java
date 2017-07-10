package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by sr on 09.07.17.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    //query url
    private String mUrl;
    //constructor
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading () {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground(){
        if (mUrl == null) {
            return null;
        }

        // perform network request, parse the response, extract list of books
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }

}
