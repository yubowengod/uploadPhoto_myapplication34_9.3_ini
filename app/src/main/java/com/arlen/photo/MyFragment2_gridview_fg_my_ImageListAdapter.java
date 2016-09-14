package com.arlen.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by GOD on 2016/9/13.
 */
public class MyFragment2_gridview_fg_my_ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private String[] imageUrls;

    public MyFragment2_gridview_fg_my_ImageListAdapter(Context context, String[] imageUrls) {
        super(context, R.layout.fg_content2_gridview_fg_my_item, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.fg_content2_gridview_fg_my_item, parent, false);
        }
        Glide
                .with(context)
                .load(imageUrls[position])
                .into((ImageView) convertView);
        return convertView;
    }
}
