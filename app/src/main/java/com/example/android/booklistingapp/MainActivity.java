package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    //declare variables needed throughout the activity

    private ListView mBookListView;
    private BookAdapter mAdapter;
    private SearchView mSearchTitle;
    private static String mBookTitleSearched;
    private TextView mEmptyStateTextView;
    private static final int BOOK_LOADER_ID = 1;
    private ProgressBar mLoadingSpinner;
    final List<Book> books = new ArrayList<Book>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find the recurring views in the layout
        mBookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        mSearchTitle = (SearchView) findViewById(R.id.search_bar);
        mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        //set the EmptyView
        mBookListView.setEmptyView(mEmptyStateTextView);

        //enable the the SearchView's SubmitButton
        mSearchTitle.setSubmitButtonEnabled(true);
        //add hint to the SearchView
        mSearchTitle.setQueryHint("Enter a title");

        //set loading spinner to GONE since it's only used after the user has started the query
        mLoadingSpinner.setVisibility(View.GONE);

        //set an OnQueryTextListener on the SearchButton
        mSearchTitle.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //get reference to ConnectivityManager and check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                //get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                //create and initialise a boolean variable for connectivity status
                final boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

                //if device is connected
                if (isConnected) {
                    //set visbility of loading spinner to visible
                    mLoadingSpinner.setVisibility(View.VISIBLE);
                    //Create a new list for the books
                    //create a new BookAdapter that takes an empty list of books as input
                    mAdapter = new BookAdapter(MainActivity.this, books);
                    Parcelable state = mBookListView.onSaveInstanceState();
                    //set the adapter to the ListView
                    mBookListView.setAdapter(mAdapter);
                    mBookListView.onRestoreInstanceState(state);
                    //restart loader and start searching
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    mSearchTitle.clearFocus();
                    return true;
                }
                //if device is not connected
                else {
                    mAdapter.clear();
                    mEmptyStateTextView.setText(R.string.no_network);
                    mLoadingSpinner.setVisibility(View.GONE);
                    return false;
                }
            }
        });

        //set an OnClickListener to every list item with an intent to open a
        //a website with more details about the current book
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Book currentBook = mAdapter.getItem(position);

                Uri bookUri = Uri.parse(currentBook.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        //get the query given by the user, replaces any spacetabs by "+" to make the query stay
        // valid when used for forming the query url
        mBookTitleSearched = mSearchTitle.getQuery().toString().replace(" ", "+");

        //create a BookLoader with it und return it
        BookLoader loader = new BookLoader(this, mBookTitleSearched);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        //hide loading spinner
        mLoadingSpinner.setVisibility(View.GONE);

        //set empty state text when no books found
        mEmptyStateTextView.setText(R.string.no_books);

        //clear adapter of previous book data
        mAdapter.clear();

        //if there is a valid list of Books, add them to the adapter's
        //data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        //loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }


}
