package com.example.shivam.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class GridViewFragment extends Fragment {

    private MovieInfo[] movieInfos;

    private ImageAdapter imageAdapter;

    private String currentSortByValue;

    private boolean restoring;

    public GridViewFragment() {
        restoring = false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Reuse the old list if params have not changed
        if (savedInstanceState != null) {

            movieInfos = (MovieInfo[]) savedInstanceState.getParcelableArray("movieInfosParcelName");
            currentSortByValue = savedInstanceState.getString("sortByValue");
            restoring = savedInstanceState.getBoolean("restoring");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gridview, container, false);
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArray("movieInfosParcelName", movieInfos);
        outState.putString("sortByValue", getCurrentSortByValue());
        outState.putBoolean("restoring", true);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateGridView();
    }


    public void updateGridView() {

        //Check if a user changes the sort criteria and movieInfos is not null
        if (movieInfos != null && !isSortByValueChanged()) {
            currentSortByValue = getCurrentSortByValue();
            showMovies();
        }
        else if(restoring) {
            restoring = false;
            showMovies();
        }
        else {
            if (isNetworkAvailable()) {
                currentSortByValue = getCurrentSortByValue();
                FetchImagesTask fetchImagesTask = new FetchImagesTask();
                fetchImagesTask.execute(currentSortByValue);
            } else {
                //Show an error message
                Toast.makeText(getActivity(), "OOPS! No Internet Connectivity", Toast.LENGTH_SHORT);
            }
        }
    }


    private void showMovies() {

        GridView gridView = (GridView) getActivity().findViewById(R.id.gridview_posters);

        imageAdapter = new ImageAdapter(getActivity(), movieInfos);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfo selectedMovie = (MovieInfo) imageAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);

                intent.putExtra("selectedMovie", selectedMovie);

                startActivity(intent);
            }
        });
    }

    private boolean isSortByValueChanged() {
        if (currentSortByValue == null)
            return true;

        return !currentSortByValue.equals(getCurrentSortByValue());
    }

    public String getCurrentSortByValue() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(getString(R.string.sort_by_key), getString(R.string.sort_by_default));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class FetchImagesTask extends AsyncTask<String, Void, MovieInfo[]> {

        private final String LOG_TAG = FetchImagesTask.class.getSimpleName();


        private MovieInfo[] getInfoFomJson(String movieInfoJsonStr) throws JSONException {

            final String baseURL = "http://image.tmdb.org/t/p/";
            //final String size = "w185";
            final String size = "w342";

            try {

                JSONObject allMovieDetails = new JSONObject(movieInfoJsonStr);
                JSONArray results = allMovieDetails.getJSONArray("results");

                MovieInfo[] infos = new MovieInfo[results.length()];

                for (int i = 0; i < results.length(); i++) {

                    infos[i] = new MovieInfo();

                    String poster_path = results.getJSONObject(i).getString("poster_path");
                    infos[i].setPosterURL(baseURL + size + poster_path);
                    infos[i].setOriginalTitle(results.getJSONObject(i).getString("original_title"));
                    infos[i].setPlotSynopsis(results.getJSONObject(i).getString("overview"));
                    infos[i].setUserRating(results.getJSONObject(i).getDouble("vote_average"));
                    infos[i].setReleaseDate(results.getJSONObject(i).getString("release_date"));

                }

                return infos;

            } catch (JSONException e) {
                Log.e("JSONException", "Error parsing the JSON : " + e.toString());
            }

            return null;
        }


        @Override
        protected MovieInfo[] doInBackground(String... params) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieInfoJsonStr = null;

            try {

                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?";
                final String SORTBY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, "88a12c2d85ae4591b2a369cfc6773344")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieInfoJsonStr = buffer.toString();

                //Log.e(LOG_TAG, movieInfoJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemtping
                // to parse it.
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }

            try {
                return getInfoFomJson(movieInfoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(MovieInfo[] infos) {

            if (infos != null) {

                movieInfos = infos;

                showMovies();
            }
        }
    }
}
