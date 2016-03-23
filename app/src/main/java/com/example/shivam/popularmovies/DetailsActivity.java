package com.example.shivam.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = this.getIntent();

        MovieInfo selectedMovie = intent.getParcelableExtra("selectedMovie");

        ImageView imageView = (ImageView) findViewById(R.id.thumbnail);
        Picasso
                .with(getApplicationContext())
                .load(selectedMovie.getPosterURL())
                .into(imageView);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(selectedMovie.getOriginalTitle());

        TextView userRating = (TextView) findViewById(R.id.userRating);
        String rating = selectedMovie.getUserRating() + "/10";
        userRating.setText(rating);


        TextView synopsis = (TextView) findViewById(R.id.synopsis);
        synopsis.setText(selectedMovie.getPlotSynopsis());

        TextView releaseDate = (TextView) findViewById(R.id.releaseDate);
        releaseDate.setText(selectedMovie.getReleaseDate());
    }

}
