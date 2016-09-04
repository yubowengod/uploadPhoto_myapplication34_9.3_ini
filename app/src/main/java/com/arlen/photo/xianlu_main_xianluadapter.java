package com.arlen.photo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by God on 2016/8/18.
 */
public class xianlu_main_xianluadapter extends ArrayAdapter<xianlu_main_xianlu> {
    private int resourceId;
    public xianlu_main_xianluadapter(Context context, int textViewResourceId, List<xianlu_main_xianlu> objcets){
        super(context,textViewResourceId,objcets);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        xianlu_main_xianlu xianlu_test = getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//        ImageView xianlu_main_xianlu_Image = (ImageView) view.findViewById(R.id.xianlu_image);
        TextView xianlu_main_xianlu_Name = (TextView) view.findViewById(R.id.xianlu_name);
//        xianlu_main_xianlu_Image.setImageResource(xianlu_test.getImageId());
        xianlu_main_xianlu_Name.setText(xianlu_test.getName());
        return view;
    }
}
