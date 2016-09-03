package com.arlen.photo.photopickup.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ps_an on 2016/3/11.
 */
public class ImageUtil {

    public static ArrayList<String>  pciture_sum_Array = new ArrayList<String>();


    public static void load(Context mContext, String uri, ImageView view, int placeholder) {


//        pciture_sum_Array.add(uri);


        Glide.with(mContext)
                .load(uri)//图片地址
                .placeholder(placeholder)
                .crossFade()
                .into(view);

    }

}
