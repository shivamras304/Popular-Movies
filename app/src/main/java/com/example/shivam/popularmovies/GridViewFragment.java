package com.example.shivam.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

    public GridViewFragment() {
    }

    private ImageAdapter imageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_gridview, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateGridView();
    }

    public void updateGridView() {
        FetchImagesTask fetchImagesTask = new FetchImagesTask();
        fetchImagesTask.execute("popularity.desc");
    }



    public class FetchImagesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchImagesTask.class.getSimpleName();



        private String[] posterUrlFomJson(String movieInfoJsonStr) throws JSONException{

            final String baseURL =  "http://image.tmdb.org/t/p/";
            final String size = "w185";
            //final String size = "w500";

            JSONObject allMovieDetails = new JSONObject(movieInfoJsonStr);
            JSONArray results = allMovieDetails.getJSONArray("results");

            String[] posterURLs = new String[results.length()];

            for(int i = 0; i < results.length(); i++) {

                //JSONObject poster_path = results.getJSONObject(i).getJSONObject("results");

                String poster_path = results.getJSONObject(i).getString("poster_path");

                posterURLs[i] = baseURL + size + poster_path;
            }

            return posterURLs;
        }


        @Override
        protected String[] doInBackground(String... params) {


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
                        .appendQueryParameter(APPID_PARAM, "my_api_key")
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
                // If the code didn't successfully get the weather data, there's no point in attemping
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
                return posterUrlFomJson(movieInfoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }



            return null;
        }

         @Override
         protected void onPostExecute(String[] posterURLs) {

             if(posterURLs != null) {
                 GridView gridView = (GridView) getActivity().findViewById(R.id.gridview_posters);
                 imageAdapter = new ImageAdapter(getActivity(), posterURLs);
                 gridView.setAdapter(imageAdapter);
             }
         }


    }
}
