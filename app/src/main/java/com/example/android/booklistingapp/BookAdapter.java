package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sr on 09.07.17.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    public ArrayList<Book> bookList;

    //creates a new BookAdapter object

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);

    }

    //inflates the ListView

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View bookView = convertView;
        if (bookView == null) {
            bookView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView title = (TextView) bookView.findViewById(R.id.title);
        title.setText(currentBook.getTitle());

        TextView authors = (TextView) bookView.findViewById(R.id.author);
        authors.setText(currentBook.getAuthors());


        return bookView;
    }
}

