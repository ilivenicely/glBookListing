package com.example.robertmccormick.googlebooklisting_rob;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String GOOGLE_BOOKS_API_REQUEST_URL
            = "https://www.googleapis.com/books/v1/volumes?q=";

    public static final String THUMBNAIL_CODE = "THUMBNAIL_CODE";
    public static final String TITLE_CODE = "TITLE_CODE";
    public static final String SUBTITLE_CODE = "SUBTITLE_CODE";
    public static final String AUTHORS_CODE = "AUTHORS_CODE";
    public static final String PUBLISHED_DATE_CODE = "PUBLISHED_DATE_CODE";
    public static final String ISBN_CODE = "ISBN_CODE";
    public static final String DESCRIPTION_CODE = "DESCRIPTION_CODE";

    private TextView mEmptyStateTextView;
    private BookAdapter mBookAdapter;
    private ProgressBar progressBar;
    private List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the ListView in the layout
        final GridView booksListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input.
        mBookAdapter = new BookAdapter(this, books);
        progressBar = findViewById(R.id.loading_indicator);

        // Set the adapter on the ListView
        // so the list can be populated in the user interface.
        booksListView.setAdapter(mBookAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current book that was clicked on.
                Book currentBook = mBookAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), BookDetailsActivity.class);
                intent.putExtra(THUMBNAIL_CODE, currentBook.getThumbnail());
                intent.putExtra(TITLE_CODE, currentBook.getTitle());
                intent.putExtra(SUBTITLE_CODE, currentBook.getSubtitle());
                intent.putExtra(AUTHORS_CODE, currentBook.getAuthors());
                intent.putExtra(PUBLISHED_DATE_CODE, currentBook.getPublishDate());
                intent.putExtra(ISBN_CODE, currentBook.getISBN_13());
                intent.putExtra(DESCRIPTION_CODE, currentBook.getDescription());
                startActivity(intent);

            }
        });

        isNetworkAvailable();

    }

    private void isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {

            BookAsyncTask t = new BookAsyncTask();
            t.execute(GOOGLE_BOOKS_API_REQUEST_URL + "''");

        } else {

            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_network);
        }
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryUtils.fetchBookData(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Book> bookData) {
            // Clear the adapter of previous earthquake data.

            // If there is a valid list of Books, then add them to the adapter's
            // data set. This wil trigger the ListView to update.
            if (bookData != null && !bookData.isEmpty()) {
                books.clear();
                books.addAll(bookData);
                mBookAdapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                BookAsyncTask task = new BookAsyncTask();
                task.execute(GOOGLE_BOOKS_API_REQUEST_URL + query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
