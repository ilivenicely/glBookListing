package com.example.robertmccormick.googlebooklisting_rob;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class BookAdapter extends ArrayAdapter<Book> {

    Picasso.Builder builder;

    public BookAdapter(Activity context, List<Book> books) {
        super(context, 0, books);
        builder = new Picasso.Builder(getContext());
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });
    }

    public static String formatAuthors(String[] authors) {
        String authorsString = "";

        for (String author : authors) {
            authorsString += author + ", ";
        }

        if(authors.length > 0)
        {
            if (authors.length < 2) {
                authorsString = authorsString.substring(0, authorsString.length() - 2);
            }
        }

        return authorsString;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView thumbnailImageView = listItemView.findViewById(R.id.book_thumbnail);

        builder.build().load(currentBook.getUrlImage()).into(thumbnailImageView);

        TextView titleTextView = listItemView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        return listItemView;
    }
}
