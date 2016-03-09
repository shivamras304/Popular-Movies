package com.example.shivam.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by shivam on 25/02/16.
 */


/*class Movie {



}*/

public class ImageAdapter extends BaseAdapter {

    String[] posterURLs;

    private Context thisContext;



    public ImageAdapter(Context context, String[] input) {
        posterURLs = input;
        thisContext = context;
    }


    @Override
    public int getCount() {
        return posterURLs.length;
    }

    @Override
    public Object getItem(int position) {
        return posterURLs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView imageView;

        ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.griditem_poster_imageview);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = convertView;
        ViewHolder viewHolder = null;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.griditem_poster, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imageView.setLayoutParams(new GridView.LayoutParams(549,550));
        viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_START);

        Picasso.with(thisContext).load(posterURLs[position]).into(viewHolder.imageView);
        return viewHolder.imageView;

    }

}
